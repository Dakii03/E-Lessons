from flask import Flask, request, jsonify
import sqlite3
import datetime
app = Flask(__name__)

@app.route('/odobri_termin', methods=['POST'])
def odobri_termin():
    try:
        data = request.get_json()
        id = data.get('id')
       

        if not id:
            return jsonify({"message": "id not provided"}), 400
        print(id)

        conn = sqlite3.connect('baza.db')
        cursor = conn.cursor()
        print(id)
        # Retrieve all trainers from the Trener table
        cursor.execute("UPDATE Termini SET odobrenje = 1 WHERE id = ?", (id,))
        conn.commit()
        print(id)
        conn.close()


        return "odobreno"

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers" + str(e)})

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers"}), 477


@app.route('/odbij_termin3', methods=['POST'])
def odbij_termin2():
    try:
        data = request.get_json()
        id = data.get('id')
        print(id)

        if not id:
            return jsonify({"message": "id not provided"}), 400
        print(id)

        conn = sqlite3.connect('baza.db')
        cursor = conn.cursor()
        print(id)
        # Retrieve all trainers from the Trener table
        cursor.execute("delete from termini where id = ?", (id,))
        conn.commit()
        print(id)
        conn.close()


        return "odbijeno"

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers" + str(e)})

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers"}), 477
    
@app.route('/izbrisi_trenera', methods=['POST'])
def izbrisi_trenera():
    try:
        data = request.get_json()
        username = data.get('korisnicko_ime')

        if not username:
            return jsonify({"message": "Username not provided"}), 400
        print(username)

        conn = sqlite3.connect('baza.db')
        cursor = conn.cursor()

        # Retrieve all trainers from the Trener table
        cursor.execute("delete from korisnik_trener where korisnicko_ime = ?", (username,))

        conn.commit()
        conn.close()
        return "200"

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers" + str(e)})

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers"}), 477


@app.route('/get_termini_trenera2', methods=['POST'])
def get_termini_trenera2():
    try:
        data = request.get_json()
        username = data.get('korisnicko_ime')

        if not username:
            return jsonify({"message": "Username not provided"}), 400
        print(username)

        conn = sqlite3.connect('baza.db')
        cursor = conn.cursor()

               # Get the current date and time
        current_date = datetime.datetime.now().date()
        #current_time = datetime.datetime.now().time().strftime('%H:%M')
        current_time = (datetime.datetime.now() + datetime.timedelta(hours=1)).time().strftime('%H:%M')
        print(current_date.month)
        # Retrieve termini that haven't expired
        cursor.execute("SELECT * FROM termini WHERE korisnicko_ime_tr = ? AND odobrenje = 1 "
                        "AND ((mesec > ?) OR (mesec = ? AND dan > ?) "
                        "OR (mesec = ? AND dan = ? AND vreme_zavrsetka >= ?))",
                        (username, current_date.month, current_date.month, current_date.day, current_date.month, current_date.day, current_time))

        termini = cursor.fetchall()

        conn.close()

        # Convert the list of trainers to a list of dictionaries
        termini_lista = []
        for termin in termini:
            termini_dict = {
                "ID": termin[0],
                "dan": termin[1],
                "mesec": termin[2],
                "vreme_pocetka": termin[3],
                "vreme_zavrsetka": termin[4],
                "naziv_predmeta": termin[5],
                "korisnicko_ime": termin[6]


            }
            termini_lista.append(termini_dict)

        return jsonify(termini_lista)

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers" + str(e)})

    except Exception as e:
        return jsonify({"message": "Error while fetching trainers"}), 477
