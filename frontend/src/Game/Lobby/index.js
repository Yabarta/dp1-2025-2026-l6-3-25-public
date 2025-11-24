import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import useWebSocket from '../../hooks/useWebSocket';
import tokenService from '../../services/token.service';
import '../../static/css/lobby/lobby.css';

const initialLobbyState = [];

const Lobby = () => {
    const [lobbies, setLobbies] = useState(initialLobbyState);
    const [joinCode, setJoinCode] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const lobbyUpdates = useWebSocket('/app/lobbies/list', '/topic/lobbies');
    const jwt = tokenService.getLocalAccessToken();
    const isAuthenticated = Boolean(jwt);

    useEffect(() => {
        const fetchLobbies = async () => {
            try {
                const response = await api.get('/api/v1/matches/notStarted');
                setLobbies(response.data.map(normaliseMatchToLobby));
            } catch (err) {
                console.error('Unable to fetch lobbies', err);
                setError('No se pudieron cargar las salas disponibles.');
            }
        };

        fetchLobbies();
    }, []);

    useEffect(() => {
        if (lobbyUpdates) {
            setLobbies(Array.isArray(lobbyUpdates) ? lobbyUpdates : []);
        }
    }, [lobbyUpdates]);

    const handleCreateLobby = async (isPrivate) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await api.post(`/api/v1/matches?isPrivate=${isPrivate}`);
            const created = response.data;
            navigate(`/lobby/${created.id}`);
        } catch (err) {
            console.error('Error creating lobby', err);
            setError('No se pudo crear la sala.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleJoinLobby = async (lobbyId, code) => {
        setIsLoading(true);
        setError(null);
        try {
            await api.put(`/api/v1/matches/${lobbyId}${code ? `?code=${code}` : ''}`);
            navigate(`/lobby/${lobbyId}`);
        } catch (err) {
            console.error('Error joining lobby', err);
            setError('No se pudo unir a la sala.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleJoinByCode = async () => {
        const formattedCode = joinCode.trim().toUpperCase();
        if (!formattedCode || formattedCode.length !== 4) {
            setError('Introduce un código válido de 4 letras.');
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            const matchResponse = await api.get(`/api/v1/matches/code/${formattedCode}`);
            const match = matchResponse.data;
            await handleJoinLobby(match.id, formattedCode);
        } catch (err) {
            console.error('Error joining private lobby', err);
            setError('No se encontró una sala con ese código.');
            setIsLoading(false);
        }
    };

    const availableLobbies = useMemo(() => lobbies.map((lobby) => ({
        ...lobby,
        players: lobby.players ?? [],
    })), [lobbies]);

    return (
        <div className="lobby-page">
            {error && <div className="lobby-error-banner">{error}</div>}
            <div className="lobby-layout">
                <section className="lobby-card lobby-card--primary">
                    <header className="lobby-header">
                        <div>
                            <h1>Salas disponibles</h1>
                            <p className="lobby-subtitle">Crea una sala o únete a otra para empezar una partida online.</p>
                        </div>
                        <div className={`lobby-status ${isLoading ? 'lobby-status--loading' : ''}`}>
                            <span className="lobby-status-dot" />
                            <span>{isLoading ? 'Actualizando...' : 'Sincronizado'}</span>
                        </div>
                    </header>
                    <div className="lobby-actions">
                        <button className="lobby-button" onClick={() => handleCreateLobby(false)} disabled={isLoading || !isAuthenticated}>
                            Crear sala pública
                        </button>
                        <button className="lobby-button lobby-button--secondary" onClick={() => handleCreateLobby(true)} disabled={isLoading || !isAuthenticated}>
                            Crear sala privada
                        </button>
                    </div>
                    <section className="lobby-list">
                        {availableLobbies.length === 0 ? (
                            <div className="lobby-empty">
                                <h2>No hay salas disponibles</h2>
                                <p>Inicia la primera sala o espera a que otro jugador cree una.</p>
                            </div>
                        ) : (
                            <ul>
                                {availableLobbies.filter(lobby => !lobby.isPrivate).map((lobby) => (
                                    <li key={lobby.id} className="lobby-item">
                                        <div className="lobby-item-header">
                                            <div className="lobby-item-title">
                                                <span className="lobby-name">Sala #{lobby.id}</span>
                                            </div>
                                            <span className="lobby-capacity">{lobby.players.length}/2 jugadores</span>
                                        </div>
                                        <div className="lobby-item-body">
                                            <div className="lobby-player-chips">
                                                {lobby.players.length > 0 ? (
                                                    lobby.players.map((player) => (
                                                        <span key={player.id} className="lobby-chip">{player.nickname || player.username}</span>
                                                    ))
                                                ) : (
                                                    <span className="lobby-chip lobby-chip--empty">Esperando jugadores...</span>
                                                )}
                                            </div>
                                            <button
                                                className="lobby-button lobby-button--join"
                                                onClick={() => handleJoinLobby(lobby.id)}
                                                disabled={isLoading || lobby.players.length >= 2 || !isAuthenticated}
                                            >
                                                Unirse
                                            </button>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </section>
                </section>
                <aside className="lobby-card lobby-card--secondary">
                    <h2>¿Tienes un código?</h2>
                    <p>Introduce el código de cuatro letras que te haya compartido tu amigo para unirte directamente.</p>
                    <div className="lobby-join-by-code">
                        <input
                            type="text"
                            value={joinCode}
                            onChange={(event) => {
                                const value = event.target.value.toUpperCase();
                                setJoinCode(value);
                                if (error) {
                                    setError(null);
                                }
                            }}
                            placeholder="Código privado"
                            maxLength={4}
                        />
                        <button className="lobby-button" onClick={handleJoinByCode} disabled={isLoading || !isAuthenticated}>
                            Unirse por código
                        </button>
                    </div>
                    <ul className="lobby-hints">
                        <li>Crea una sala privada para compartirla con amigos.</li>
                        <li>Solo puedes estar en una sala a la vez.</li>
                        <li>El estado se actualiza automáticamente en tiempo real.</li>
                    </ul>
                </aside>
            </div>
        </div>
    );
};

function normaliseMatchToLobby(match) {
    return {
        id: match.id,
        code: match.code,
        isPrivate: Boolean(match.code),
        players: [match.player1, match.player2]
            .filter(Boolean)
            .map((player) => ({
                id: player.id,
                nickname: player.nickname,
                username: player.user?.username,
            })),
    };
}

export default Lobby;
