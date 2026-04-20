import socket
import time

SERVER_HOST = '127.0.0.1'
SERVER_PORT = 3000

def synchronize_clock():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        # t0: antes de enviar
        t0 = time.time()
        
        # s.connect((server_dir, server_port))
        s.connect((SERVER_HOST, SERVER_PORT))
        s.sendall(b"TIME_REQUEST")

        # recibir tiempo del servidor
        data = s.recv(1024)

        # t1: después de recibir
        t1 = time.time()

    # convertir tiempo recibido
    T = float(data.decode())

    # calcular RTT y delay
    RTT = t1 - t0
    delay = RTT / 2

    # tiempo ajustado
    adjusted_time = T + delay

    print(f"Tiempo servidor: {T}")
    print(f"RTT: {RTT}")
    print(f"Delay estimado: {delay}")
    print(f"Tiempo ajustado: {adjusted_time}")
    print(f"Tiempo local actual: {time.time()}")

    # offset (cuánto deberías corregir tu reloj)
    offset = adjusted_time - time.time()
    print(f"Offset a aplicar: {offset} segundos")

if __name__ == "__main__":
    synchronize_clock()