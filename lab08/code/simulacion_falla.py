import psycopg2
import time

DB_CONFIG = {
    "arequipa": {"host": "localhost", "port": 5431, "dbname": "banco_arequipa", "user": "banco", "password": "banco123", "connect_timeout": 3},
    "cusco": {"host": "localhost", "port": 5433, "dbname": "banco_cusco", "user": "banco", "password": "banco123", "connect_timeout": 3}
}

def get_conn(nodo):
    # Agregamos un timeout corto para que la falla de red se detecte rápido
    conn = psycopg2.connect(**DB_CONFIG[nodo])
    conn.autocommit = True
    return conn

def consultar_saldos(titulo="SALDOS ACTUALES"):
    print(f"\n=== {titulo} ===")
    for nodo, cliente in [("arequipa", "Cliente A"), ("cusco", "Cliente B")]:
        try:
            conn = get_conn(nodo)
            with conn.cursor() as cur:
                cur.execute("SELECT saldo FROM cuentas WHERE cliente = %s", (cliente,))
                row = cur.fetchone()
                print(f"  {nodo.upper()} - {cliente}: S/ {row[0]:,.2f}")
            conn.close()
        except Exception as e:
            print(f"  {nodo.upper()}: [NODO INACCESIBLE - FALLA DE RED]")
    print("=" * 30)

def simulacion_caida_2pc(monto=25000):
    print("\n>>> INICIANDO TRANSFERENCIA CON SIMULACIÓN DE FALLO")
    tx_id = "tx_simulacion"
    
    conn_origen = get_conn("arequipa")
    conn_destino = get_conn("cusco")

    try:
        print("\n[FASE 1 - PREPARE]")
        # Preparamos Arequipa
        with conn_origen.cursor() as cur:
            cur.execute("BEGIN")
            cur.execute("UPDATE cuentas SET saldo = saldo - %s WHERE cliente = 'Cliente A'", (monto,))
            cur.execute(f"PREPARE TRANSACTION '{tx_id}_origen'")
        print("  ✓ AREQUIPA: PREPARED (S/ 25,000 descontados y bloqueados)")

        # Preparamos Cusco
        with conn_destino.cursor() as cur:
            cur.execute("BEGIN")
            cur.execute("UPDATE cuentas SET saldo = saldo + %s WHERE cliente = 'Cliente B'", (monto,))
            cur.execute(f"PREPARE TRANSACTION '{tx_id}_destino'")
        print("  ✓ CUSCO: PREPARED (S/ 25,000 listos para acreditarse)")

        # ==========================================
        # PUNTO CRÍTICO: SIMULACIÓN DE FALLA
        # ==========================================
        print("\n" + "!"*50)
        print("ALTO AHÍ: ESTAMOS EN EL PUNTO CRÍTICO (DOUBT PHASE)")
        print("Abre otra terminal y ejecuta uno de estos comandos:")
        print("  Para tirar la red:  docker network disconnect lab08_red-bancaria pg-cusco")
        print("  Para apagar nodo:   docker stop pg-cusco")
        print("!"*50)
        input(">>> Presiona ENTER SOLO CUANDO hayas ejecutado el comando de falla... ")

        print("\n[FASE 2 - COMMIT]")
        
        # El coordinador (este script) hace commit en Arequipa
        with conn_origen.cursor() as cur:
            cur.execute(f"COMMIT PREPARED '{tx_id}_origen'")
        print("  ✓ AREQUIPA: COMMITTED Exitosamente")

        # El coordinador intenta hacer commit en Cusco... ¡Pero fallará!
        print("  Intentando contactar a CUSCO para el COMMIT...")
        with conn_destino.cursor() as cur:
            cur.execute(f"COMMIT PREPARED '{tx_id}_destino'")
        print("  ✓ CUSCO: COMMITTED")

    except psycopg2.OperationalError as e:
        print(f"\n[ERROR CRÍTICO CAPTURADO] Se perdió conexión con Cusco durante la Fase 2.")
        print("El dinero salió de Arequipa, pero no llegó a Cusco. ¡Tenemos una inconsistencia!")
    finally:
        if conn_origen and not conn_origen.closed: conn_origen.close()
        if conn_destino and not conn_destino.closed: conn_destino.close()

def recuperar_transaccion():
    print("\n>>> INICIANDO PROCESO DE RECUPERACIÓN (RECOVERY MANAGER)")
    print("Intentando reconectar con Cusco para aplicar la transacción huérfana...")
    
    intentos = 1
    while intentos <= 5:
        try:
            # Intentamos conectarnos a Cusco
            conn = get_conn("cusco")
            with conn.cursor() as cur:
                # Verificamos si la transacción sigue pendiente
                cur.execute("SELECT gid FROM pg_prepared_xacts WHERE gid = 'tx_simulacion_destino'")
                if cur.fetchone():
                    print("  [INFO] Transacción 'tx_simulacion_destino' encontrada en el WAL. Aplicando COMMIT...")
                    cur.execute("COMMIT PREPARED 'tx_simulacion_destino'")
                    print("  ✓ RECUPERACIÓN EXITOSA: Los S/ 25,000 han sido acreditados en Cusco.")
                else:
                    print("  [INFO] No hay transacciones huérfanas por recuperar.")
            conn.close()
            return
        except psycopg2.OperationalError:
            print(f"  [Reintento {intentos}/5] Cusco sigue inaccesible. Esperando red...")
            time.sleep(3)
            intentos += 1
    print("  [FALLA] No se pudo recuperar Cusco. Se requiere intervención manual.")

if __name__ == "__main__":
    consultar_saldos("SALDOS ANTES DE LA FALLA")
    
    simulacion_caida_2pc()
    
    consultar_saldos("SALDOS INCONSISTENTES (DURANTE LA FALLA)")
    
    print("\n" + "!"*50)
    print("LA BASE DE DATOS ESTÁ ROTA AHORA MISMO.")
    print("Arequipa tiene menos dinero, pero Cusco no lo recibió.")
    print("Abre tu otra terminal y restaura el nodo:")
    print("  Para restaurar red:  docker network connect lab08_red-bancaria pg-cusco")
    print("  Para encender nodo:  docker start pg-cusco")
    print("!"*50)
    input(">>> Presiona ENTER SOLO CUANDO el nodo Cusco esté arriba de nuevo... ")

    recuperar_transaccion()

    consultar_saldos("SALDOS FINALES RECUPERADOS")