import { useEffect, useState } from "react"
import ChartComparator from "../components/ChartComparator";
import { useParams } from "react-router-dom";
import jwt_decode from "jwt-decode";
import tokenService from "../../services/token.service";


export default function Comparator (props) {
    const jwt = tokenService.getLocalAccessToken();
    
    const { username } = useParams()
    const [currentPlayer, setCurrentPlayer] = useState(() => {
            if (!jwt) return username ?? "";
            try {
                return jwt_decode(jwt)?.sub ?? (username ?? "");
            } catch (e) {
                console.error("Invalid JWT", e);
                return username ?? "";
            }
        });

    const [currentPlayerStats, setCurrentPlayerStats] = useState(null)
    const [searchTerm, setSearchTerm] = useState('')
    const [opponent, setOpponent] = useState(null);
    const [opponentStats, setOpponentStats] = useState([0, 0, 0, 0, 0, 0])

    const [error, setError] = useState('');
    
    useEffect(() => {
        // Cargar estadísticas del jugador actual al iniciar
        if (currentPlayer) {
            fetchCurrentPlayerStats();
        }
    }, [currentPlayer]);

    const fetchCurrentPlayerStats = async () => {
        try {
            const playerResponse = await fetch(`/api/v1/players/user/${encodeURIComponent(currentPlayer)}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}` 
                }
            });
            const playerData = await playerResponse.json();
            
            if (playerData && playerData.id) {
                const statsResponse = await fetch(`/api/v1/players/${playerData.id}/statistics`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${jwt}` 
                    }
                });
                const stats = await statsResponse.json();
                setCurrentPlayerStats(stats);
            }
        } catch (err) {
            console.error('Error al cargar estadísticas del jugador actual:', err);
        }
    };
    
    const fetchOpponentData = async (opponentName) => {
        const opponentURL = opponentName ? `/api/v1/players/nickname/${encodeURIComponent(opponentName)}` : "";
        try {
            const response = await fetch(opponentURL, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}` 
                }
            });
            const data = await response.json();
            return data;
        }catch(err) {
            setError(err.message);
        }
    };

    const fetchOpponentStats = async (playerId) => {
        try {
            const response = await fetch(`/api/v1/players/${playerId}/statistics`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwt}` 
                }
            });
            const stats = await response.json();
            return stats;
        } catch (err) {
            console.error('Error al cargar estadísticas del oponente:', err);
            return null;
        }
    };

    // Función para transformar las estadísticas al formato del gráfico
    const transformStatsToChartFormat = (stats) => {
        if (!stats) return null;
        return [
            stats.bacteriasCreated || 0,
            stats.gamesPlayed || 0,
            stats.gamesWon || 0,
            (stats.gamesPlayed || 0) - (stats.gamesWon || 0), // Partidas perdidas
            stats.sarcinasCreated || 0,
            (stats.timePlayed || 0) / 60 // Tiempo en minutos
        ];
    };

    // Función para obtener el máximo valor de ambas listas de estadísticas
    const getMaxStatValue = () => {
        const myStats = transformStatsToChartFormat(currentPlayerStats);
        const opponentChartStats = transformStatsToChartFormat(opponentStats);
        
        let maxValue = 10; // Valor por defecto
        
        if (myStats) {
            maxValue = Math.max(maxValue, Math.max(...myStats));
        }
        
        if (opponentChartStats) {
            maxValue = Math.max(maxValue, Math.max(...opponentChartStats));
        }
        
        return Math.ceil(maxValue / 10) * 10;
    };

    const handleSearch = async () => {
        if (!searchTerm) return;
        
        try {
            const opponentData = await fetchOpponentData(searchTerm.trim());
            if (opponentData && opponentData.nickname && opponentData.id) {
                setOpponent(opponentData);
                
                const stats = await fetchOpponentStats(opponentData.id);
                setOpponentStats(stats);
                
                setError('');
            } else {
                setOpponent(null);
                setOpponentStats([0, 0, 0, 0, 0, 0]);
                setError('Jugador no encontrado. Verifica el username.');
            }
        } catch (err) {
            setOpponent(null);
            setOpponentStats(null);
            setError('Error al buscar el jugador. Verifica el username.');
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
            myName={currentPlayer}
            myStats={transformStatsToChartFormat(currentPlayerStats)} 
            opponentStats={transformStatsToChartFormat(opponentStats)}
            opponentName={opponent ? opponent.nickname : null}
            maxValue={getMaxStatValue()}
        />
      </div>

    </div>
  );
}