import React, { useEffect, useRef, useState, useCallback, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import jwt_decode from "jwt-decode";
import tokenService from "../services/token.service";
import '../static/css/profile/profile.css';
import bacteria from '../static/images/bacteria.png';
import useFetchState from "../util/useFetchState";
import getErrorModal from "../util/getErrorModal";
import ProfileHeader from './components/ProfileHeader';
import StatsSection from './components/StatsSection';
import RecentGames from './components/RecentGames';
import AchievementsSection from './components/AchievementsSection';
import HistoryPopup from './components/HistoryPopup';
import EditPopup from './components/EditPopup';
import * as Yup from 'yup';
// Button is used inside extracted components

// Constants
const DEFAULT_PROFILE_PIC = bacteria;

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
    const navigate = useNavigate();

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
            const sanitizedName = name.toLowerCase();
            const lowerCamelCaseName = sanitizedName.replace(/_([a-z])/g, (g) => g[1].toUpperCase());
            const value = playerStats?.[lowerCamelCaseName];
            if (value === null || value === undefined) return 0;
            return value;
        
    }, [playerStats]);

    const achievementProgress = useCallback((achievement) => {
        const progress = Math.round(getStatValue(achievement.statisticName) || 0);
        const target = achievement.valor;
        return `${Math.min(progress, target)}/${target}`;
    }, [getStatValue]);


    const modal = getErrorModal(setVisible, visible, message);
    const isLoading = playerLoading || gamesLoading || achievementsLoading || userAchievementsLoading || statsLoading;
    const playerExists = useMemo(() => {
        return playerData && Object.keys(playerData).length > 0 && playerData.id;
    }, [playerData]);

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


    // Loading state
    if (isLoading) {
        if (!playerExists) navigate('/');
            
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
                <ProfileHeader 
                    playerData={playerData}
                    currentPlayer={currentPlayer} 
                    username={username} 
                    setShowEditPopup={setShowEditPopup} 
                />
                <div className="bg">
                    { currentPlayer && currentPlayer===username ? 
                        <img src={profilePic} onClick={handleChangeProfilePicture} alt="provisional" className="profilePicture" /> :
                        <img src={profilePic} alt="provisional" className="profilePicture" />
                    } 
                    { currentPlayer && currentPlayer===username && 
                        <input type="file" ref={imageInputRef} onChange={handleFileChange} className="hiddenFileInput" accept="image/*" /> 
                    }
                    <StatsSection 
                        playerData={playerData} 
                        playerStats={playerStats} 
                    />
                </div>
            </div>
            <div className="right">
                <RecentGames
                    userGames={userGames}
                    isWinner={isWinner}
                    duracion={duracion}
                    getPlayerProfilePic={getPlayerProfilePic}
                    handleNavigateToProfile={navigateToProfile}
                    setShowHistoryPopup={setShowHistoryPopup}
                    navigate={navigate}
                />
                <AchievementsSection 
                    Achievements={Achievements} 
                    UserAchievements={UserAchievements} 
                    achievementProgress={achievementProgress} 
                />
            </div>
            <HistoryPopup 
                showHistoryPopup={showHistoryPopup} 
                setShowHistoryPopup={setShowHistoryPopup} 
                userGames={userGames} isWinner={isWinner} 
                getPlayerProfilePic={getPlayerProfilePic} 
                handleNavigateToProfile={navigateToProfile} 
                duracion={duracion} 
                navigate={navigate}
            />
            <EditPopup 
                showEditPopup={showEditPopup} 
                setShowEditPopup={setShowEditPopup} 
                validationSchema={validationSchema} 
                playerData={playerData} 
                handleEditSubmit={handleEditSubmit} 
            />
        </div>
    );
}
export const navigateToProfile = async (nickname, navigate) => {
    try {
        const res = await fetch(`/api/v1/players/nickname/${encodeURIComponent(nickname)}`);
        const user = await res.json();
        
        navigate(`/profile/${encodeURIComponent(user.username ?? nickname)}`);
    } catch (err) {
        console.error('Unable to go to profile', err);
    }
};