import { useState } from "react"
import ChartComparator from "../individual/ChartComparator";



export default function Comparator (props) {
    const [searchTerm, setSearchTerm] = useState('')
    const [opponent, setOpponent] = useState(null); // Guardará { name:Str, stats:[] }
    const [error, setError] = useState('');

    const myName = 'player1'
    const myStats = [80, 45, 90, 60, 20, 75];

    const mockDatabase = {
        'player2': { name: 'Player Two', stats: [40, 50, 60, 30, 60, 50] },
        'pro_gamer': { name: 'The Legend', stats: [99, 95, 99, 98, 10, 99] },
        'bacterio': { name: 'Dr. Bacterio', stats: [10, 100, 50, 20, 80, 100] }
    };

    const handleSearch = () => {
        if (!searchTerm) return;
        
        // Simular búsqueda (aquí iría tu fetch a la API)
        const found = mockDatabase[searchTerm.toLowerCase()];

        if (found) {
            setOpponent(found);
            setError('');
        } else {
            setOpponent(null);
            setError('Jugador no encontrado. Prueba con "player2" o "bacterio"');
        }
    };

    return (
    <div style={{ padding: '40px', backgroundColor: '#222', minHeight: '100vh', color: 'white' }}>
        <h1 style={{ textAlign: 'center' }}>Comparador de Jugadores</h1>

        <div style={{ display: 'flex', justifyContent: 'center', margin: '30px 0', gap: '10px' }}>
            <input
                type="text"
                placeholder="Buscar rival..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                style={{ padding: '10px', borderRadius: '5px', border: 'none', width: '200px' }}
            />
            <button 
                onClick={handleSearch}
                style={{ padding: '10px 20px', borderRadius: '5px', border: 'none', background: '#f1c40f', cursor: 'pointer', fontWeight: 'bold' }}
            >
                Comparar
            </button>
        </div>

    {error && <p style={{ color: '#e74c3c', textAlign: 'center' }}>{error}</p>}
      <div style={{ maxWidth: '800px', margin: '0 auto' }}>
        <ChartComparator 
            myName={myName}
            myStats={myStats} 
            opponentStats={opponent ? opponent.stats : null}
            opponentName={opponent ? opponent.name : null}
        />
      </div>

    </div>
  );
}