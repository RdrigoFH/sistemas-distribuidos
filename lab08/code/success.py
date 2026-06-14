import psycopg2

DB_CONFIG = {
    "arequipa": {
        "host": "localhost", "port": 5431,  # Puerto confirmado como correcto
        "dbname": "banco_arequipa", "user": "banco", "password": "banco123"
    },
    "cusco": {
        "host": "localhost", "port": 5433,
        "dbname": "banco_cusco", "user": "banco", "password": "banco123"
    },
    "trujillo": {
        "host": "localhost", "port": 5434,
        "dbname": "banco_trujillo", "user": "banco", "password": "banco123"
    }
}

def get_conn(nodo):
    return psycopg2.connect(**DB_CONFIG[nodo])

def consultar_saldos():
    """Muestra saldos de todas las sedes."""
    print("\n=== SALDOS ACTUALES ===")
    cuentas = [
        ("arequipa", "Cliente A"),
        ("cusco", "Cliente B"),
        ("trujillo", "Cliente C")
    ]
    for nodo, cliente in cuentas:
        try:
            conn = get_conn(nodo)
            with conn.cursor() as cur:
                cur.execute("SELECT saldo FROM cuentas WHERE cliente = %s", (cliente,))
                row = cur.fetchone()
                if row is None:
                    print(f"  {nodo.upper()} - {cliente}: [NO ENCONTRADO]")
                else:
                    print(f"  {nodo.upper()} - {cliente}: S/ {row[0]:,.2f}")
            conn.close()
        except Exception as e:
            print(f"  {nodo.upper()}: [ERROR] {e}")
    print("=" * 30)

def transferir_2pc(monto, cliente_origen, cliente_destino, nodo_origen, nodo_destino):
    """
    Ejecuta transferencia con protocolo Two-Phase Commit.
    Usa autocommit=True siempre y BEGIN manual para evitar desincronización del driver.
    """
    conn_origen = None
    conn_destino = None

    try:
        conn_origen = get_conn(nodo_origen)
        conn_destino = get_conn(nodo_destino)

        # MANTENEMOS autocommit=True durante TODA la ejecución
        conn_origen.autocommit = True
        conn_destino.autocommit = True

        # Verificar fondos
        with conn_origen.cursor() as cur:
            cur.execute("SELECT saldo FROM cuentas WHERE cliente = %s", (cliente_origen,))
            saldo = cur.fetchone()[0]
            if saldo < monto:
                print(f"[ABORT] Fondos insuficientes. Saldo: S/ {saldo:,.2f}")
                conn_origen.close()
                conn_destino.close()
                return
            print(f"[INFO] Saldo origen verificado: S/ {saldo:,.2f}")

        # FASE 1: PREPARE
        print("\n[FASE 1 - PREPARE]")

        # --- Origen ---
        with conn_origen.cursor() as cur:
            cur.execute("BEGIN") # Iniciamos transacción manualmente en SQL
            cur.execute(
                "UPDATE cuentas SET saldo = saldo - %s WHERE cliente = %s",
                (monto, cliente_origen)
            )
            cur.execute("PREPARE TRANSACTION 'tx_origen'")
        print(f"  ✓ {nodo_origen}: PREPARED (descuento de S/ {monto:,.2f} pendiente)")

        # --- Destino ---
        with conn_destino.cursor() as cur:
            cur.execute("BEGIN") # Iniciamos transacción manualmente en SQL
            cur.execute(
                "UPDATE cuentas SET saldo = saldo + %s WHERE cliente = %s",
                (monto, cliente_destino)
            )
            cur.execute("PREPARE TRANSACTION 'tx_destino'")
        print(f"  ✓ {nodo_destino}: PREPARED (incremento de S/ {monto:,.2f} pendiente)")

        # FASE 2: COMMIT
        print("\n[FASE 2 - COMMIT]")
        
        with conn_origen.cursor() as cur:
            cur.execute("COMMIT PREPARED 'tx_origen'")
        print(f"  ✓ {nodo_origen}: COMMITTED")

        with conn_destino.cursor() as cur:
            cur.execute("COMMIT PREPARED 'tx_destino'")
        print(f"  ✓ {nodo_destino}: COMMITTED")

        print(f"\n[OK] Transferencia de S/ {monto:,.2f} completada exitosamente.")
        print(f"     {cliente_origen} ({nodo_origen}) → {cliente_destino} ({nodo_destino})")

    except Exception as e:
        print(f"\n[ERROR] Fallo en la transacción: {e}")
        print("[ROLLBACK] Intentando revertir...")

        # Intentar rollback de transacciones preparadas
        for conn, nombre, tx_id in [
            (conn_origen, nodo_origen, 'tx_origen'),
            (conn_destino, nodo_destino, 'tx_destino')
        ]:
            if conn and not conn.closed:
                try:
                    # autocommit ya está en True, enviamos el comando directo
                    with conn.cursor() as cur:
                        cur.execute(f"ROLLBACK PREPARED '{tx_id}'")
                    print(f"  ✓ {nombre}: ROLLED BACK PREPARED")
                except Exception as rb_err:
                    if "does not exist" in str(rb_err):
                        # Intentar rollback normal por si se quedó en el BEGIN sin llegar al PREPARE
                        try:
                            with conn.cursor() as cur:
                                cur.execute("ROLLBACK")
                            print(f"  ✓ {nombre}: ROLLED BACK NORMAL (limpio)")
                        except:
                            pass
                    else:
                        print(f"  ✗ {nombre}: {rb_err}")

    finally:
        for conn in [(conn_origen, "origen"), (conn_destino, "destino")]:
            if conn[0] and not conn[0].closed:
                conn[0].close()

if __name__ == "__main__":
    print("=" * 50)
    print("SISTEMA NACIONAL DE BANCOS COOPERATIVOS")
    print("Protocolo: Two-Phase Commit (Corregido)")
    print("=" * 50)

    consultar_saldos()

    print("\n>>> Ejecutando transferencia: S/ 25,000 de Arequipa → Cusco")
    transferir_2pc(
        monto=25000,
        cliente_origen="Cliente A",
        cliente_destino="Cliente B",
        nodo_origen="arequipa",
        nodo_destino="cusco"
    )

    consultar_saldos()

    # Restaurar valores originales
    print("\n>>> Restaurando valores originales...")
    for nodo, cliente, saldo in [
        ("arequipa", "Cliente A", 100000),
        ("cusco", "Cliente B", 50000)
    ]:
        conn = get_conn(nodo)
        conn.autocommit = True
        with conn.cursor() as cur:
            cur.execute("UPDATE cuentas SET saldo = %s WHERE cliente = %s", (saldo, cliente))
        conn.close()
    print("[OK] Valores restaurados.")
    consultar_saldos()