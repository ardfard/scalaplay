# Create a database with the data stored in the current directory
initdb -D .tmp/mydb --no-locale --encoding=UTF-8

# Start PostgreSQL running as the current user
# and with the Unix socket in the current directory
pg_ctl -D .tmp/mydb -l logfile -o "--unix_socket_directories='$PWD'" start

