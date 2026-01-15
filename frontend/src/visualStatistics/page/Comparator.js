import { useEffect, useState } from "react"
import ChartComparator from "../components/ChartComparator";
import ChartBoxPlot from "../components/ChartBoxPlot";
import { useParams } from "react-router-dom";
import jwt_decode from "jwt-decode";
import tokenService from "../../services/token.service";
import '../styles/Comparator.css'
import useFetchState from "../../util/useFetchState";

export default function Comparator (props) {
    const jwt = tokenService.getLocalAccessToken();
    
    const [message, setMessage] = useState(null)
    const [visible, setVisible] = useState(false)
    
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

    const [gamesDistribution, setGameDistribution] = useFetchState(
        [],
        `/api/v1/statistics/distribution/gamesPlayed`,
        jwt,
        setMessage,
        setVisible
    )

    const [timePlayedDistribution, setTimePlayedDistributionDistribution] = useFetchState(
        [],
        `/api/v1/statistics/distribution/timePlayed`,
        jwt,
        setMessage,
        setVisible
    )

    useEffect(() => {
        if (currentPlayer) fetchCurrentPlayerStats();
    }, [currentPlayer]);

    const fetchCurrentPlayerStats = async () => {
        try {
            const playerResponse = await fetch(`/api/v1/players/user/${encodeURIComponent(currentPlayer)}`, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
            });
            const playerData = await playerResponse.json();
            
            if (playerData && playerData.id) {
                const statsResponse = await fetch(`/api/v1/players/${playerData.id}/statistics`, {
                    method: 'GET',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
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
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
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
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
            });
            const stats = await response.json();
            return stats;
        } catch (err) {
            console.error('Error al cargar estadísticas del oponente:', err);
            return null;
        }
    };

    const transformStatsToChartFormat = (stats) => {
        if (!stats) return [0, 0, 0, 0, 0, 0];
        return [
            stats.bacteriasCreated / stats.gamesPlayed|| 0,
            stats.gamesPlayed || 0,
            stats.gamesWon || 0,
            (stats.gamesPlayed || 0) - (stats.gamesWon || 0),
            stats.sarcinasCreated || 0,
            (stats.timePlayed || 0) / 60 / 60
        ];
    };

    const getMaxStatValue = () => {
        const myStats = transformStatsToChartFormat(currentPlayerStats);
        const opponentChartStats = transformStatsToChartFormat(opponentStats);
        let maxValue = 10;
        if (myStats && myStats.length > 0) maxValue = Math.max(maxValue, Math.max(...myStats));
        if (opponentChartStats && opponentChartStats.length > 0) maxValue = Math.max(maxValue, Math.max(...opponentChartStats));
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
            setError('Error al buscar el jugador.');
        }
    };


    return (
        <div className="comparator-container">
            <h1 className="comparator-title">
                PETRIS <span className="text-green">COMPARADOR</span>
            </h1>
            <div className="boxplot-container" style={{gap: '20px', maxWidth: '1000px', margin: '0 auto 40px auto', alignSelf: 'center'}}>
    
                {/* Gráfico de Caja 1: Partidas */}
                <div className="glass-card" style={{ padding: '20px'}}>
                    <ChartBoxPlot 
                        title="Distribución de Partidas"
                        yAxisTitle="Nº Partidas"
                        dataDistribution={gamesDistribution}
                        userValue={currentPlayerStats?.gamesPlayed || 0}
                    />
                </div>

                    {/* Gráfico de Caja 2: Sarcinas */}
                <div className="glass-card" style={{ padding: '20px' }}>
                    <ChartBoxPlot 
                        title="Tiempo de juego (h)"
                        yAxisTitle="Tiempo por persona (h)"
                        dataDistribution={timePlayedDistribution}
                        userValue={Math.round((((currentPlayerStats?.timePlayed ?? 0) / 60) / 60) * 100) / 100}
                    />
                </div>

            </div>
            <div className="glass-card">
                <h2 style={{textAlign: 'center', marginBottom: '20px', color: '#00ff9d', fontSize: '1.2rem'}}>COMPARADOR DE JUGADORES</h2>
                
                <div className="search-container">
                    <input
                        type="text"
                        placeholder="Buscar rival..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                    <button 
                        onClick={handleSearch}
                        className="neon-button"
                    >
                        Comparar
                    </button>
                </div>

                {error && <p className="error-message">{error}</p>}
                
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