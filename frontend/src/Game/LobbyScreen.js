import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import useWebSocket from '../hooks/useWebSocket';
import api from '../services/api';
import tokenService from '../services/token.service';
import '../static/css/lobby/lobby.css';

const currentUser = tokenService.getUser();
console.log("Current user in LobbyScreen:", currentUser);
let player = ''
if(currentUser && currentUser.roles.includes('PLAYER')){
    player = (await api.get(`/api/v1/players/user/${currentUser.username}`)).data;
}


export default function LobbyScreen() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [lobby, setLobby] = useState(null);
    const [error, setError] = useState(null);
    const [isBusy, setIsBusy] = useState(false);
    const [canStart, setCanStart] = useState(false);
    const lobbyUpdate = useWebSocket('/app/lobbies/watch', `/topic/lobby/${id}`, Number(id));
    const skipCleanupRef = useRef(false);
    const latestLobbyRef = useRef(null);

    useEffect(() => {
        latestLobbyRef.current = lobby;
    }, [lobby]);

    useEffect(() => {
        const fetchLobby = async () => {
            try {
                const response = await api.get(`/api/v1/matches/${id}`);
                setLobby(normaliseMatchToLobby(response.data));
            } catch (err) {
                console.error('Unable to fetch lobby', err);
                setError('No se pudo cargar la sala.');
            }
        };
        fetchLobby();
    }, [id]);

    useEffect(() => {
        if (!lobbyUpdate) {
            return;
        }
        if (typeof lobbyUpdate === 'string' && lobbyUpdate === 'LOBBY_CLOSED') {
            setError('La sala se ha cerrado.');
            navigate('/lobby', { replace: true });
            return;
        }
        setLobby((prev) => {
            const normalised = normaliseLobbyPayload(lobbyUpdate);
            return {
                ...normalised,
                version: (prev?.version ?? 0) + 1,
            };
        });
    }, [lobbyUpdate, navigate]);

    useEffect(() => {
        if (lobby?.startedAt) {
            skipCleanupRef.current = true;
            navigate(`/game/${id}`);
        }
    }, [lobby, id, navigate]);

    const leaveLobbySilently = useCallback(() => {
        const snapshot = latestLobbyRef.current;
        if (!snapshot || snapshot.startedAt) {
            return;
        }
        if (currentUser) {
            const isParticipant = snapshot.players.some((player) => {
                if (!player) {
                    return false;
                }
                const sameId = currentUser.id && player.id === currentUser.id;
                const sameUsername = currentUser.username && player.username === currentUser.username;
                const sameNickname = currentUser.nickname && player.nickname === currentUser.nickname;
                return Boolean(sameId || sameUsername || sameNickname);
            });
            if (!isParticipant) {
                return;
            }
        }
        (async () => {
            try {
                await api.put(`/api/v1/matches/${id}/leave`);
            } catch (err) {
                console.error('Unable to auto-abandon lobby on navigation', err);
            }
        })();
    }, [id]);

    useEffect(() => {
        return () => {
            if (skipCleanupRef.current) {
                return;
            }
            leaveLobbySilently();
        };
    }, [leaveLobbySilently]);

    const handleLeaveLobby = async () => {
        setError(null);
        setIsBusy(true);
        try {
            await api.put(`/api/v1/matches/${id}/leave`);
            skipCleanupRef.current = true;
            navigate('/lobby');
        } catch (err) {
            console.error('Unable to leave lobby', err);
            setError('No se pudo abandonar la sala.');
        } finally {
            setIsBusy(false);
        }
    };

    const handleStartLobby = async () => {
        setError(null);
        setIsBusy(true);
        try {
            await api.put(`/api/v1/matches/${id}/start`);
            skipCleanupRef.current = true;
            navigate(`/game/${id}`);
        } catch (err) {
            console.error('Unable to start match', err);
            setError('No se pudo iniciar la partida.');
        } finally {
            setIsBusy(false);
        }
    };

    const isCreator = useMemo(() => {
        if (!lobby || !currentUser) {
            return false;
        }
        return player.id === lobby.creatorId;
    }, [lobby]);

    useEffect(() => {
        const ready = Boolean(lobby) && isCreator && lobby.players.length >= 2;
        setCanStart(ready);
    }, [lobby, isCreator]);

    if (!lobby) {
        return (
            <div className="lobby-page lobby-page--centered">
                <div className="lobby-loading-card">Cargando sala...</div>
            </div>
        );
    }

    const isPrivateLobby = Boolean(lobby.isPrivate && lobby.code);

    return (
        <div className="lobby-page lobby-page--detail">
            {error && <div className="lobby-error-banner">{error}</div>}
            <div className="lobby-detail-layout">
                <section className="lobby-card lobby-card--primary lobby-detail-card">
                    <header className="lobby-header lobby-header--detail">
                        <div>
                            <h1>Sala #{lobby.id}</h1>
                            <p className="lobby-subtitle">
                                {isPrivateLobby ? 'Sala privada lista para tus amigos' : 'Sala pública esperando rivales'}
                            </p>
                        </div>
                        <div className={`lobby-status ${lobby.startedAt ? 'lobby-status--in-progress' : ''}`}>
                            <span className="lobby-status-dot" />
                            <span>{lobby.startedAt ? 'Partida en curso' : 'Lobby abierto'}</span>
                        </div>
                    </header>

                    <div className="lobby-detail-info">
                        <div className="lobby-detail-pill">
                            <span className="lobby-pill-label">Jugadores</span>
                            <span className="lobby-pill-value">{lobby.players.length}/2</span>
                        </div>
                        <div className="lobby-detail-pill">
                            <span className="lobby-pill-label">Tipo</span>
                            <span className="lobby-pill-value">{isPrivateLobby ? 'Privada' : 'Pública'}</span>
                        </div>
                        {isPrivateLobby && (
                            <div className="lobby-detail-pill">
                                <span className="lobby-pill-label">Código</span>
                                <span className="lobby-pill-value lobby-pill-code">{lobby.code}</span>
                            </div>
                        )}
                    </div>

                    <section className="lobby-detail-players">
                        <h2>Jugadores en la sala</h2>
                        <ul className="lobby-detail-player-list">
                            {lobby.players.map((player) => (
                                <li key={player.id ?? player.username} className="lobby-detail-player-item">
                                    <span className="lobby-chip">{player.nickname ?? player.username}</span>
                                    {player.id === lobby.creatorId && <span className="lobby-badge">Creador</span>}
                                </li>
                            ))}
                            {lobby.players.length < 2 && (
                                <li className="lobby-detail-player-item lobby-detail-player-item--empty">
                                    <span className="lobby-chip lobby-chip--empty">Asiento libre</span>
                                    <span className="lobby-badge lobby-badge--muted">Esperando jugador</span>
                                </li>
                            )}
                        </ul>
                    </section>

                    <div className="lobby-detail-actions">
                        <button
                            type="button"
                            className="lobby-button lobby-button--danger"
                            onClick={handleLeaveLobby}
                            disabled={isBusy}
                        >
                            Abandonar sala
                        </button>
                        {canStart && (
                            <button
                                type="button"
                                className="lobby-button lobby-button--highlight"
                                onClick={handleStartLobby}
                                disabled={isBusy}
                            >
                                Iniciar partida
                            </button>
                        )}
                    </div>
                </section>
                <aside className="lobby-card lobby-card--secondary lobby-detail-sidebar">
                    <h2>Consejos rápidos</h2>
                    <ul className="lobby-hints">
                        <li>Solo el creador puede iniciar la partida.</li>
                        <li>Comparte el código para que tus amigos se unan.</li>
                        <li>Sal de la sala si quieres crear o unirte a otra.</li>
                    </ul>
                </aside>
            </div>
        </div>
    );
}

function normaliseMatchToLobby(match) {
    return {
        id: match.id,
        creatorId: match.creator?.id ?? match.creatorId,
        isPrivate: Boolean(match.code),
        code: match.code,
        players: [match.player1, match.player2]
            .filter(Boolean)
            .map((player) => ({
                id: player.id,
                nickname: player.nickname,
                username: player.user?.username,
            })),
        startedAt: match.startedAt,
    };
}

function normaliseLobbyPayload(payload) {
    if (!payload) {
        return payload;
    }

    if (payload.players && !Array.isArray(payload.players)) {
        payload.players = Object.values(payload.players);
    }

    return {
        id: payload.id,
        creatorId: payload.creatorId,
        isPrivate: Boolean(payload.isPrivate),
        code: payload.code,
        players: (payload.players ?? []).map((player) => ({
            id: player.id,
            nickname: player.nickname,
            username: player.username,
        })),
        startedAt: payload.startedAt,
    };
}
