import React, { useEffect, useRef, useState, useCallback, useMemo } from "react";
import { useParams } from "react-router-dom";
import jwt_decode from "jwt-decode";
import tokenService from "../services/token.service";
import '../static/css/profile/profile.css';
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';

// Constants
const DEFAULT_PROFILE_PIC = "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png";

export default function ProfileScreen() {
    // State declarations
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
    const imageInputRef = useRef(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [showHistoryPopup, setShowHistoryPopup] = useState(false);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [profilePic, setProfilePic] = useState(DEFAULT_PROFILE_PIC);

    // Data fetching
    const playerUrl = username ? `/api/v1/players/user/${encodeURIComponent(username)}` : "";
    const [playerData, setPlayerData, playerLoading] = useFetchState({}, playerUrl, jwt, setMessage, setVisible);
    const [games, , gamesLoading] = useFetchState([], `/api/v1/matches`, jwt, setMessage, setVisible);
    const [userGames, setUserGames] = useState([]);
    const [Achievements, , achievementsLoading] = useFetchState([], `/api/v1/achievements`, jwt, setMessage, setVisible);
    const userAchievementsUrl = playerData?.id ? `/api/v1/players/${playerData.id}/achievements` : "";
    const [UserAchievements, , userAchievementsLoading] = useFetchState([], userAchievementsUrl, jwt, setMessage, setVisible, playerData?.id);
    const statsUrl = playerData?.id ? `/api/v1/players/${playerData.id}/statistics` : "";
    const [playerStats, , statsLoading] = useFetchState([], statsUrl, jwt, setMessage, setVisible, playerData?.id);

    // Effects
    useEffect(() => {
        const userGamesFiltered = games.filter(game => game.endedAt && (game.player1.id === playerData.id || game.player2.id === playerData.id));
        setUserGames(userGamesFiltered);
    }, [games, playerData]);

    useEffect(() => {
        setProfilePic(playerData.profilePicture || DEFAULT_PROFILE_PIC);
    }, [playerData]);

    // Utility functions
    const getPlayerProfilePic = useCallback((player) => player.profilePicture || DEFAULT_PROFILE_PIC, []);

    const duracion = useCallback((game) => {
        const createdAt = new Date(game.createdAt);
        const endedAt = new Date(game.endedAt);
        return Math.floor((endedAt.getTime() - createdAt.getTime()) / 60000);
    }, []);

    const isWinner = useCallback((game) => {
        const isPlayer1 = game.player1.id === playerData.id;
        return (game.winner === 1 && isPlayer1) || (game.winner === 2 && !isPlayer1);
    }, [playerData.id]);

    const getStatValue = useCallback((name) => {
        if (!playerStats) return 0;
        const stat = playerStats.find(s => s.name?.toLowerCase() === name.toLowerCase());
        return stat ? stat.valor : 0;
    }, [playerStats]);

    const achievementProgress = useCallback((achievement) => {
        const progress = Math.round(getStatValue(achievement.statisticName) || 0);
        const target = achievement.valor;
        return `${Math.min(progress, target)}/${target}`;
    }, [getStatValue]);

    // Computed values
    const hoursPlayed = useMemo(() => {
        if (!userGames.length) return 0;
        return userGames.reduce((total, game) => total + duracion(game), 0) / 60;
    }, [userGames, duracion]);

    const modal = getErrorModal(setVisible, visible, message);
    const isLoading = playerLoading || gamesLoading || achievementsLoading || userAchievementsLoading || statsLoading;

    // Event handlers
    const handleChangeProfilePicture = () => imageInputRef.current.click();

    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // Optional: Basic validation (e.g., file type and size)
        if (!file.type.startsWith('image/')) {
            setMessage('Por favor, selecciona un archivo de imagen válido.');
            setVisible(true);
            return;
        }
        if (file.size > 5 * 1024 * 1024) { // 5MB limit example
            setMessage('El archivo es demasiado grande. Máximo 5MB.');
            setVisible(true);
            return;
        }

        const formData = new FormData();
        formData.append('profilePicture', file);

        try {
            const response = await fetch(`/api/v1/players/${playerData.id}`, {
                method: 'PUT',
                headers: {
                    Authorization: `Bearer ${jwt}`,
                },
                body: formData,
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                const msg = errorData.message || `Error ${response.status}`;
                setMessage(msg);
                setVisible(true);
                return;
            }

            const updatedPlayer = await response.json();
            setPlayerData(updatedPlayer);
            setProfilePic(updatedPlayer.profilePicture || DEFAULT_PROFILE_PIC);
            setMessage('Foto de perfil actualizada exitosamente.');
            setVisible(true);
            window.location.reload();
        } catch (error) {
            setMessage('Error al subir la imagen. Inténtalo de nuevo.');
            setVisible(true);
        }
    };

    const handleEditSubmit = (values) => {
        const updatedPlayer = { ...playerData, ...values };
        setPlayerData(updatedPlayer);
        setShowEditPopup(false);

        fetch(`/api/v1/players/${updatedPlayer.id || playerData.id}`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedPlayer),
        })
            .then((response) => {
                if (!response.ok) {
                    return response.json().then((json) => {
                        const msg = json?.message || `Error ${response.status}`;
                        setMessage(msg);
                        setVisible(true);
                        throw new Error(msg);
                    });
                }
                return response.json().catch(() => ({}));
            })
            .then((json) => {
                if (json?.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    window.location.reload();
                }
            })
            .catch((err) => {
                const msg = err?.message || 'Error updating profile';
                setMessage(msg);
                setVisible(true);
            });
    };

    // Validation schema
    const validationSchema = Yup.object().shape({
        nickname: Yup.string()
            .max(255, 'El nombre de usuario es demasiado largo')
            .required('El nombre de usuario es requerido'),
        email: Yup.string()
            .email('Por favor, introduce un correo electrónico válido')
            .required('El correo electrónico es requerido'),
    });

    // Sub-components
    const ProfileHeader = () => (
        <div>
            <div className="profileHeader">
                <span className="profileNickname">{playerData.nickname}</span>
                {currentPlayer && currentPlayer===username && <span onClick={() => setShowEditPopup(true)} className="editIcon">✏️</span>}
            </div>
            <div className="profileHeaderEmail">{playerData.email}</div>
        </div>
    );

    const StatsSection = () => (
        <div className="mainStatContainer">
            <div className="statItem">
                <span className="statLabel">Fecha de Creación</span>
                <span className="statValue">{playerData.createdAt ? new Date(playerData.createdAt).toLocaleDateString() : new Date().toLocaleDateString()}</span>
            </div>
            <div className="statItem">
                <span className="statLabel">Tiempo de Juego</span>
                <span className="statValue">{Math.floor(hoursPlayed)} horas y {Math.round((hoursPlayed - Math.floor(hoursPlayed)) * 60)} minutos</span>
            </div>
            <div className="statItem">
                <span className="statLabel">Partidas Online</span>
                <span className="statValue">{userGames.length}</span>
            </div>
            <div className="statItem">
                <span className="statLabel">Victorias</span>
                <span className="statValue">{getStatValue('games_won')}</span>
            </div>
            <div className="statItem">
                <span className="statLabel">Derrotas</span>
                <span className="statValue">{userGames.filter(game => !isWinner(game)).length}</span>
            </div>
            <div className="statItem">
                <span className="statLabel">Sarcinas</span>
                <span className="statValue">{getStatValue('sarcines_created')}</span>
            </div>
        </div>
    );

    const RecentGames = () => (
        <div className="bg">
            <h1 className="title">Partidas Recientes</h1>
            <div className="recentGamesContainer">
                {userGames.slice(0, 3).map(game => (
                    <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                        <div className="gameHeader">
                            <div className="gameResult">
                                {isWinner(game) ? "Victoria" : "Derrota"}
                                <span className="gameTurns">({game.turn} turnos)</span>
                            </div>
                            <span className="gameDate">Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}</span>
                        </div>
                        <div className="gamePlayersContainer">
                            <div className="scorePlayer1">
                                <div className="gamePlayerInfo player2Info">
                                    <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                                </div>
                                <div className="score">{game.finalP2Score}</div>
                            </div>
                            <span className="gameVs">vs</span>
                            <div className="scorePlayer2">
                                <div className="gamePlayerInfo player1Info">
                                    {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                                </div>
                                <div className="score">{game.finalP1Score}</div>
                            </div>
                        </div>
                        <div className="gameDetailsContainer">
                            <div className="gameDetail">Código de la partida: {game.code}</div>
                            <div className="gameDetail">Duración: {duracion(game)} mins</div>
                        </div>
                    </div>
                ))}
            </div>
            <div className="watchHistoryContainer">
                <button className="watchHistoryButton" onClick={() => setShowHistoryPopup(true)}>Ver Historial</button>
            </div>
        </div>
    );

    const AchievementsSection = () => (
        <div className="bg">
            <h1 className="title">Logros</h1>
            <h4>Completado {UserAchievements.length}/{Achievements.length}</h4>
            <div className="mainStatContainer">
                {Achievements.map(achievement => {
                    const isCompleted = UserAchievements.some(a => a.id === achievement.id);
                    return (
                        <div key={achievement.id} className={`achievement ${isCompleted ? 'completed' : ''}`}>
                            <div className="achievementHeader">
                                <img src={achievement.icon} alt={achievement.name} className="achievementIcon" />
                                <h3 className="achievementName">{achievement.name}</h3>
                                <p className="achievementProgress">{achievementProgress(achievement)}</p>
                            </div>
                            <div className="achievementInfo">
                                <p className="achievementDescriptionContainer achievementDescription">{achievement.description}</p>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );

    const HistoryPopup = () => (
        showHistoryPopup && (
            <div className="popupOverlay">
                <div className="popupContent">
                    <h2 className="title">Historial de Partidas</h2>
                    <button onClick={() => setShowHistoryPopup(false)} className="closePopupButton">X</button>
                    <div className="gamesList">
                        {userGames.length > 0 ? userGames.map(game => (
                            <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                                <div className="gameHeader">
                                    <div className="gameResult">
                                        {isWinner(game) ? "Victoria" : "Derrota"}
                                        <span className="gameTurns">({game.turns} turnos)</span>
                                    </div>
                                    <span className="gameDate">Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}</span>
                                </div>
                                <div className="gamePlayersContainer">
                                    <div className="gamePlayerInfo">
                                        <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                                    </div>
                                    <span className="gameVs">vs</span>
                                    <div className="gamePlayerInfo">
                                        {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                                    </div>
                                </div>
                                <div className="gameDetailsContainer">
                                    <div className="gameDetail">Código de la partida: {game.code}</div>
                                    <div className="gameDetail">Puntuación: {game.score}</div>
                                    <div className="gameDetail">Duración: {duracion(game)} mins</div>
                                </div>
                            </div>
                        )) : <p>No hay partidas para mostrar.</p>}
                    </div>
                </div>
            </div>
        )
    );

    const EditPopup = () => (
        showEditPopup && (
            <div className="popupOverlay">
                <div className="popupContent">
                    <h2 className="title">Editar Perfil</h2>
                    <button onClick={() => setShowEditPopup(false)} className="closePopupButton">X</button>
                    <Formik
                        initialValues={{ nickname: playerData.nickname, email: playerData.email }}
                        validationSchema={validationSchema}
                        onSubmit={handleEditSubmit}
                    >
                        {({ isSubmitting }) => (
                            <Form>
                                <div className="formGroup">
                                    <label htmlFor="nickname">Nombre de usuario</label>
                                    <Field name="nickname" type="text" className="formControl" />
                                    <ErrorMessage name="nickname" component="div" className="error" />
                                </div>
                                <div className="formGroup">
                                    <label htmlFor="email">Email</label>
                                    <Field name="email" type="email" className="formControl" />
                                    <ErrorMessage name="email" component="div" className="error" />
                                </div>
                                <div className="formButtons">
                                    <button type="submit" className="editProfileButton" disabled={isSubmitting}>Guardar Cambios</button>
                                    <button type="button" className="watchHistoryButton" onClick={() => setShowEditPopup(false)}>Cancelar</button>
                                </div>
                            </Form>
                        )}
                    </Formik>
                </div>
            </div>
        )
    );

    // Loading state
    if (isLoading) {
        return (
            <div className="loadingOverlay">
                {modal}
                <div className="loadingCard">
                    <div className="loadingTitle">Cargando datos del perfil</div>
                    <div className="loadingSubtitle">Un momento, estamos cargando tu información personal y logros… ⏳</div>
                </div>
            </div>
        );
    }

    // Main render
    return (
        <div className="profileContainer">
            {modal}
            <div className="left">
                <ProfileHeader />
                <div className="bg">
                    { currentPlayer && currentPlayer===username ? <img src={profilePic} onClick={handleChangeProfilePicture} alt="provisional" className="profilePicture" /> :
                    <img src={profilePic} alt="provisional" className="profilePicture" />} 
                    { currentPlayer && currentPlayer===username && <input type="file" ref={imageInputRef} onChange={handleFileChange} className="hiddenFileInput" accept="image/*" /> }
                    <StatsSection />
                </div>
            </div>
            <div className="right">
                <RecentGames />
                <AchievementsSection />
            </div>
            <HistoryPopup />
            <EditPopup />
        </div>
    );
}