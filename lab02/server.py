# core idea
# the client try to sync his clock asking for the time to the server 
# this that take a while
# So this algorithm adds adds a correction based on round trip time RTT

import socket
import time

PORT = 3000
HOST = "0.0.0.0" # -> comodin is for listen for everything

def open_server():
    # AF_INET -> IPv4 
    # SOCK_STREAM -> TCP protocol
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))    # link the host name with the port
        s.listen()
        print("the server is listening")

        while True:
            # stop the program and when receive something 
            # addr -> return the IP and the port that is comming from 
            # conn -> an socket object, is like the channel communication
            conn, addr = s.accept()
            print(conn, addr)

            with conn:
                print(f"conexion from {addr}")
                conn.recv(1024) # receive a message up to 1024 bits

                server_time = time.time() # get the current time of the server
                
                conn.sendall(str(server_time).encode()) # Los sockets solo envían bytes, no texto. Por eso, convertimos el número a string y luego a bytes con .encode()

                # conn.sendall() sent the information to the client


if __name__ == "__main__":
    open_server()

    



            
