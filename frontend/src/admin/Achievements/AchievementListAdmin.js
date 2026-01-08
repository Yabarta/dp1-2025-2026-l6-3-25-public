import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import AchievementHeader from "./AchievementHeader";
import AchievementCreateModal from "./AchievementCreateModal";
import AchievementEditModal from "./AchievementEditModal";
import AchievementGrid from "./AchievementGrid";
import "../../static/css/admin/achievementListAdmin.css";
import jwt_decode from "jwt-decode";
import AchievementProgressBar from "./AchievementProgressBar";


const jwt = tokenService.getLocalAccessToken();

export default function AchievementListAdmin() {
    const Statistics = ["games_played", "games_won", "sarcines_created", "bacterias_created", "time_played"];
    const isAdmin = jwt ? jwt_decode(jwt).authorities.includes("ADMIN") : false;
    
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [searchName, setSearchName] = useState("");
    const [alerts, setAlerts] = useState([]);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [editingAchievement, setEditingAchievement] = useState(null);
    const [achievements, setAchievements] = useFetchState(
        [],
        `/api/v1/achievements`,
        jwt,
        setMessage,
        setVisible
    );

    useEffect(() => {

    }, [jwt])

    useEffect(() => {
        if (!jwt) {
            navigate('/login');
        }
    }, [navigate]);
    const filteredAchievements = achievements.filter((achievement) =>
        achievement.name.toLowerCase().includes(searchName.toLowerCase())
    );

    const handleEdit = (achievement) => {
        setEditingAchievement(achievement);
        setEditModalOpen(true);
    };

    const handleSaveCondition = (achievement, conditionValue) => {
        // El modal se encarga de guardar los datos ahora
        // Solo actualizamos el estado local
        const updatedAchievements = achievements.map((a) =>
            a.id === achievement.id ? achievement : a
        );
        setAchievements(updatedAchievements);
        setEditModalOpen(false);
        setEditingAchievement(null);
    };

    const handleDelete = (id) => {
        deleteFromList(
            `/api/v1/achievements/${id}`,
            id,
            [achievements, setAchievements],
            [alerts, setAlerts],
            setMessage,
            setVisible
        );
    };

    const handleSaveNewAchievement = (newAchievementData, achievementImage) => {
        if (!newAchievementData.name || !newAchievementData.description || !newAchievementData.statisticName || !newAchievementData.valor) {
            setMessage("Por favor completa todos los campos");
            setVisible(true);
            return;
        }

        const valorValue = parseInt(newAchievementData.valor, 10);
        if (isNaN(valorValue)) {
            setMessage("El valor debe ser un número válido");
            setVisible(true);
            return;
        }

        const formData = new FormData();
        formData.append('name', newAchievementData.name);
        formData.append('description', newAchievementData.description);
        formData.append('statisticName', newAchievementData.statisticName);
        formData.append('valor', valorValue);
        
        // Agregar imagen si existe
        if (newAchievementData.imageFile) {
            formData.append('image', newAchievementData.imageFile);
        }

        fetch(`/api/v1/achievements`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${jwt}`,
            },
            body: formData,
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    setAchievements([...achievements, json]);
                    setCreateModalOpen(false);
                    setMessage("Logro creado exitosamente");
                    setVisible(true);
                }
            })
            .catch((err) => {
                setMessage("Error al crear el logro" + err.message);
                setVisible(true);
            });
    };
    const modal = getErrorModal(setVisible, visible, message);
    return (
        <div className="achievement-admin-container">
            <AchievementHeader 
                searchName={searchName} 
                setSearchName={setSearchName}
                onCreateClick={() => setCreateModalOpen(true)}
                isAdmin={isAdmin}
            />
            {alerts.map((a) => a.alert)}
            {modal}
            <AchievementCreateModal 
                isOpen={createModalOpen}
                toggle={() => setCreateModalOpen(!createModalOpen)}
                onSave={handleSaveNewAchievement}
                statistics={Statistics}
            />
            <AchievementEditModal 
                isOpen={editModalOpen}
                toggle={() => setEditModalOpen(!editModalOpen)}
                achievement={editingAchievement}
                onSave={handleSaveCondition}
                statistics={Statistics}
            />
            <AchievementGrid 
                achievements={filteredAchievements}
                editingAchievement={editingAchievement}
                onEdit={handleEdit}
                onDelete={handleDelete}
                isAdmin={isAdmin}
            />
        </div>
    );
}