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

const jwt = tokenService.getLocalAccessToken();

export default function AchievementListAdmin() {
    const Statistics = ["games_played", "games_won", "sarcines_created", "bacterias_created", "time_played"];
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
        const parsedValue = parseInt(conditionValue, 10);
        if (isNaN(parsedValue)) {
            setMessage("Por favor ingresa un número válido");
            setVisible(true);
            return;
        }
        const updatedAchievement = {
            ...achievement,
            valor: parsedValue
        };

        fetch(`/api/v1/achievements/${achievement.id}`, {
            method: "PUT",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(updatedAchievement),
        })
            .then((response) => response.json())
            .then((json) => {
                if (json.message) {
                    setMessage(json.message);
                    setVisible(true);
                } else {
                    const updatedAchievements = achievements.map((a) =>
                        a.id === achievement.id ? updatedAchievement : a
                    );
                    setAchievements(updatedAchievements);
                    setEditModalOpen(false);
                    setEditingAchievement(null);
                }
            })
            .catch((err) => {
                setMessage("Error al actualizar el logro" + err.message);
                setVisible(true);
            });
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

    const handleSaveNewAchievement = (newAchievementData) => {
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

        const achievementData = {
            name: newAchievementData.name,
            description: newAchievementData.description,
            image: "imagelin.png",
            statisticName: newAchievementData.statisticName,
            valor: valorValue
        };

        fetch(`/api/v1/achievements`, {
            method: "POST",
            headers: {
                Authorization: `Bearer ${jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
            body: JSON.stringify(achievementData),
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
            />
        </div>
    );
}