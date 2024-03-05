import sqlite3
import datetime

conn = sqlite3.connect('baza.db')
cursor = conn.cursor()
username = 'adonis'
        # Get the current date and time
current_date = datetime.datetime.now().date()
current_time = datetime.datetime.now().time().strftime('%H:%M')
print(current_date.month)
        # Retrieve termini that haven't expired
cursor.execute(
    "SELECT * FROM termini WHERE korisnicko_ime_tr = ? AND odobrenje = 0 "
    "AND ((mesec > ?) OR (mesec = ? AND dan > ?) "
    "OR (mesec = ? AND dan = ? AND vreme_zavrsetka >= ?))",
    (username, current_date.month, current_date.month,
     current_date.day, current_date.month, current_date.day, current_time))

termini = cursor.fetchall()
print(termini)
conn.close()