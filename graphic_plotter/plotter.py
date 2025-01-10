import pandas as pd
import matplotlib.pyplot as plt

def plot_frame_time(file_path):
    """
    Citește datele dintr-un fișier text și creează un grafic liniar pentru timpul în funcție de numărul de cadre.

    file_path: str - Calea către fișierul cu date.
    """
    try:
        # Citește fișierul
        data = pd.read_csv(file_path, sep='\t')

        # Coloanele
        frames = data['Frames']
        timp = data['Timp']

        # Crearea graficului
        plt.figure(figsize=(10, 6))
        plt.plot(frames, timp, marker='o', label='Timp (ms)', color='green')

        # Adăugarea detaliilor la grafic
        plt.title('Timpul în Funcție de Numărul de Cadre', fontsize=14)
        plt.xlabel('Număr de Cadre (Frames)', fontsize=12)
        plt.ylabel('Timpul (ms)', fontsize=12)
        plt.grid(linestyle='--', alpha=0.6)
        plt.legend(fontsize=10)

        # Afișarea graficului
        plt.tight_layout()
        plt.show()
    except Exception as e:
        print(f"Eroare la procesarea fișierului: {e}")

# Exemplu de apel al funcției
file_path = "data.txt"  # Fișierul cu datele
plot_frame_time(file_path)
