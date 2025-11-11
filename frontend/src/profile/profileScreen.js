import React, { useEffect, useRef, useState, useCallback } from "react";
import jwt_decode from "jwt-decode";
import tokenService from "../services/token.service";
import '../static/css/profile/profile.css';
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';

export default function ProfileScreen() {
    const jwt = tokenService.getLocalAccessToken();
    
    const imageInputRef = useRef(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [username] = useState(() => jwt ? jwt_decode(jwt).sub : "");

    const playerUrl = username ? `/api/v1/players/user/${encodeURIComponent(username)}` : "";
    const [playerData, setPlayerData, playerLoading] = useFetchState({}, playerUrl, jwt, setMessage, setVisible);
    const [games, , gamesLoading] = useFetchState(
        [],
        `/api/v1/matches`,
        jwt,
        setMessage,
        setVisible,
    );
    const [userGames, setUserGames] = useState([]);

    const [Achievements, , achievementsLoading] = useFetchState(
        [{ id: 1, name: "Primera Victoria", description: "Gana tu primera partida.", icon: "https://example.com/icons/first_win.png", StatisticValueName: "gamesWon", valor: 1 },
        { id: 2, name: "Veterano", description: "Juega 50 partidas.", icon: "https://example.com/icons/veteran.png", StatisticValueName: "gamesPlayed", valor: 50 },
        ],
        `/api/v1/achievements`,
        jwt,
        setMessage,
        setVisible,
    );

    const userAchievementsUrl = playerData && playerData.id ? `/api/v1/players/${playerData.id}/achievements` : "";
    const [UserAchievements, , userAchievementsLoading] = useFetchState([], userAchievementsUrl, jwt, setMessage, setVisible, playerData && playerData.id ? playerData.id : null);

    useEffect(() => {
        const userGamesFiltered = games.filter(game => game.endedAt ? (game.player1.id === playerData.id || game.player2.id === playerData.id) : false);
        setUserGames(userGamesFiltered);
    }, [games, playerData]);

    const statsUrl = playerData && playerData.id ? `/api/v1/players/${playerData.id}/statistics` : "";
    const [playerStats, , statsLoading] = useFetchState([], statsUrl, jwt, setMessage, setVisible, playerData && playerData.id ? playerData.id : null);
    
    useEffect(() => {
        setProfilePic(playerData.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png");
    }, [playerData]);

    const modal = getErrorModal(setVisible, visible, message);

    

    const getPlayerProfilePic = (player) => {
        return player.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png";
    };
    const duracion = (game) => {
        const createdAt = new Date(game.createdAt);
        const endedAt = new Date(game.endedAt);
        return Math.floor((endedAt.getTime() - createdAt.getTime()) / 60000);
    }

    const hoursPlayed = userGames && userGames.length ? userGames.reduce((total, game) => total + duracion(game), 0) / 60 : 0;

    const isWinner = (game) => {
        const isPlayer1 = game.player1.id === playerData.id;
        return (game.winner === 1 && isPlayer1) || (game.winner === 2 && !isPlayer1);
    };

    const getStatValue = useCallback((name) => {
        const key = name;
        if (playerStats) {
            const stat = playerStats.find(s => 
                {
                    const props = [s.name].filter(Boolean).map(p => String(p).toLowerCase());
                    return props.includes(key);
                });
            if (stat) return stat.valor;
        }
        return 0;
    }, [playerStats]);

    const achievementProgress = (achievement) => {
        const statName = achievement.statisticName;
        const raw = getStatValue(statName);
        const progress = Math.round(raw ?? 0);
        return `${progress >= (achievement.valor) ? (achievement.valor) : progress}/${achievement.valor}`;
    };

    const [profilePic, setProfilePic] = useState(playerData.profilePicture || "https://www.dsac.gov/image-repository/blank-profile-picuture.png/@@images/image.png");
    const [showHistoryPopup, setShowHistoryPopup] = useState(false);

    const handleChangeProfilePicture = () => {
        imageInputRef.current.click();
    };

    const handleFileChange = async (event) => {
        const image = event.target.files[0];
        if (image) alert(`Archivo seleccionado: ${image.name}. Aún no está implementado xd.`);
    };

    const validationSchema = Yup.object().shape({
        nickname: Yup.string()
            .max(255, 'El nombre de usuario es demasiado largo')
            .required('El nombre de usuario es requerido'),
        email: Yup.string()
            .email('Por favor, introduce un correo electrónico válido')
            .required('El correo electrónico es requerido'),
    });

    const handleEditSubmit = (values) => {
        const updatedPlayer = { ...playerData, ...values };
        setPlayerData(updatedPlayer);
        setShowEditPopup(false);

        fetch("/api/v1/players/" + (updatedPlayer.id || playerData.id), {
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
                        const msg = (json && json.message) ? json.message : `Error ${response.status}`;
                        setMessage(msg);
                        setVisible(true);
                        throw new Error(msg);
                    }).catch(() => {
                        const msg = `Error ${response.status}`;
                        setMessage(msg);
                        setVisible(true);
                        throw new Error(msg);
                    });
                }
                return response.json().catch(() => ({}));
            })
            .then((json) => {
                if (json && json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    window.location.reload();
                }
            })
            .catch((err) => {
                const msg = err && err.message ? err.message : 'Error updating profile';
                setMessage(msg);
                setVisible(true);
            });
    };
    const requiresStats = !!(playerData && playerData.id);
    const isLoading = (playerLoading || gamesLoading || achievementsLoading || userAchievementsLoading || (requiresStats && statsLoading));

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

    return (
        <div className="profileContainer">{modal}
            <div className="left">
                <div>
                    <div className="profileHeader">
                        <span className="profileNickname">{playerData.nickname}</span>
                        <span onClick={() => setShowEditPopup(true)} className="editIcon">✏️</span>
                    </div>
                    <div className="profileHeaderEmail">{playerData.email}</div>
                </div>
                <div className="bg">
                    <img src={profilePic}
                        onClick={handleChangeProfilePicture}
                        alt="provisional"
                        className="profilePicture" />
                    <input
                        type="file"
                        ref={imageInputRef}
                        onChange={handleFileChange}
                        className="hiddenFileInput"
                        accept="image/*"
                    />
                    <div className="mainStatContainer">
                        <div className="statItem">
                            <span className="statLabel">Fecha de Creación</span>
                            <span className="statValue">{playerData.createdAt? new Date(playerData.createdAt).toLocaleDateString() : new Date().toLocaleDateString()}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Tiempo de Juego</span>
                            <span className="statValue">{Math.floor(hoursPlayed) || 0} horas y {Math.round((hoursPlayed - Math.floor(hoursPlayed)) * 60) || 0} minutos</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Partidas Online</span>
                            <span className="statValue">{userGames.length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Victorias</span>
                            <span className="statValue">{getStatValue('games_won')
                                }</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Derrotas</span>
                            <span className="statValue">{userGames.filter((game) => !isWinner(game)).length || 0}</span>
                        </div>
                        <div className="statItem">
                            <span className="statLabel">Sarcinas</span>
                            <span className="statValue">{getStatValue('sarcines_created') || 0
                            }</span>
                        </div>
                    </div>
                    
                </div>
            </div>
            <div className="right">
                <div className="bg">
                    <h1 className="title">Partidas Recientes</h1>
                    <div className="recentGamesContainer">
                        {userGames.slice(0, 3).map(game => {
                            return (
                                <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                                    <div className="gameHeader">
                                        <div className="gameResult">
                                            {isWinner(game) ? "Victoria" : "Derrota"}
                                            <span className="gameTurns">
                                                ({game.turn} turnos)
                                            </span>
                                        </div>
                                        <span className="gameDate">
                                            Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                        </span>
                                    </div>
                                    <div className="gamePlayersContainer">
                                        <div className="scorePlayer1">
                                            <div className="gamePlayerInfo player2Info">
                                                <img src={getPlayerProfilePic(game.player2)} alt={game.player2.nickname} className="gamePlayerPic" /> {game.player2.nickname}
                                            </div>
                                            <div className="score">
                                                {game.finalP2Score}
                                            </div>
                                        </div>
                                        <span className="gameVs">vs</span>
                                        <div className="scorePlayer2">
                                            <div className="gamePlayerInfo player1Info">
                                                {game.player1.nickname} <img src={getPlayerProfilePic(game.player1)} alt={game.player1.nickname} className="gamePlayerPic" />
                                            </div>
                                            <div className="score">
                                                {game.finalP1Score}
                                            </div>
                                        </div>
                                    </div>
                                    <div className="gameDetailsContainer">
                                        <div className="gameDetail">
                                            Código de la partida: {game.code}
                                        </div>
                                        <div className="gameDetail">
                                            Duración: {duracion(game)} mins
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                    <div className="watchHistoryContainer">
                        <button className="watchHistoryButton" onClick={() => setShowHistoryPopup(true)}>
                            Ver Historial
                        </button>
                    </div>
                </div>
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
                                        <p className="achievementProgress">
                                            {achievementProgress(achievement)}
                                        </p>
                                    </div>
                                    <div className="achievementInfo">
                                        <div>
                                            <p className="achievementDescriptionContainer achievementDescription">
                                                {achievement.description}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
            {showHistoryPopup && (
                <div className="popupOverlay">
                    <div className="popupContent">
                        <h2 className="title">Historial de Partidas</h2>
                        <button onClick={() => setShowHistoryPopup(false)} className="closePopupButton">X</button>
                        <div className="gamesList">
                            {userGames.length > 0 ? (
                                userGames.map(game => {
                                    return (
                                        <div key={game.id} className={isWinner(game) ? "gameWinBg" : "gameLoseBg"}>
                                            <div className="gameHeader">
                                                <div className="gameResult">
                                                    {isWinner(game) ? "Victoria" : "Derrota"}
                                                    <span className="gameTurns">
                                                        ({game.turns} turnos)
                                                    </span>
                                                </div>
                                                <span className="gameDate">
                                                    Fecha de creación: {new Date(game.createdAt).toLocaleDateString()}
                                                </span>
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
                                                <div className="gameDetail">
                                                    Código de la partida: {game.code}
                                                </div>
                                                <div className="gameDetail">
                                                    Puntuación: {game.score}
                                                </div>
                                                <div className="gameDetail">
                                                    Duración: {duracion(game)} mins
                                                </div>
                                            </div>
                                        </div>
                                    );
                                })
                            ) : (
                                <p>No hay partidas para mostrar.</p>
                            )}
                        </div>
                    </div>
                </div>
            )}
            {showEditPopup && (
                <div className="popupOverlay">
                    <div className="popupContent">
                        <h2 className="title">Editar Perfil</h2>
                        <button onClick={() => setShowEditPopup(false)} className="closePopupButton">X</button>
                        <Formik
                            initialValues={{
                                nickname: playerData.nickname,
                                email: playerData.email,
                            }}
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
                                        <button type="submit" className="editProfileButton" disabled={isSubmitting}>
                                            Guardar Cambios
                                        </button>
                                        <button type="button" className="watchHistoryButton" onClick={() => setShowEditPopup(false)}>
                                            Cancelar
                                        </button>
                                    </div>
                                </Form>
                            )}
                        </Formik>
                    </div>
                </div>
            )}
        </div>
    );
}