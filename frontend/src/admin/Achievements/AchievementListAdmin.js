import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, ButtonGroup, Modal, ModalHeader, ModalBody, ModalFooter, Input, Label } from "reactstrap";
import { FaEdit, FaTrash, FaTrophy } from "react-icons/fa";
import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import "../../static/css/admin/achievementListAdmin.css";

const jwt = tokenService.getLocalAccessToken();

export default function AchievementListAdmin() {
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [searchName, setSearchName] = useState("");
    const [alerts, setAlerts] = useState([]);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [editingAchievement, setEditingAchievement] = useState(null);
    const [editCondition, setEditCondition] = useState("");
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
    }, [navigate, jwt]);

    const filteredAchievements = achievements.filter((achievement) =>
        achievement.name.toLowerCase().includes(searchName.toLowerCase())
    );

    const handleEdit = (achievement) => {
        setEditingAchievement(achievement);
        setEditCondition(achievement.condition ? achievement.condition.toString() : "");
        setEditModalOpen(true);
    };

    const handleSaveCondition = () => {
        if (!editingAchievement) return;

        const conditionValue = parseInt(editCondition, 10);
        if (isNaN(conditionValue)) {
            setMessage("Por favor ingresa un número válido");
            setVisible(true);
            return;
        }

        const updatedAchievement = {
            ...editingAchievement,
            condition: conditionValue
        };

        fetch(`/api/v1/achievements/${editingAchievement.id}`, {
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
                        a.id === editingAchievement.id ? updatedAchievement : a
                    );
                    setAchievements(updatedAchievements);
                    setEditModalOpen(false);
                    setEditingAchievement(null);
                    setEditCondition("");
                }
            })
            .catch((err) => {
                setMessage("Error al actualizar el logro");
                setVisible(true);
            });
    };

    const handleCloseModal = () => {
        setEditModalOpen(false);
        setEditingAchievement(null);
        setEditCondition("");
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

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="achievement-admin-container">
            <div className="achievement-header">
                <div className="achievement-title-section">
                    <FaTrophy className="achievement-icon" />
                    <h1 className="achievement-title">Gestionar Logros</h1>
                </div>
                <input
                    type="text"
                    placeholder="Buscar logro..."
                    className="achievement-search"
                    value={searchName}
                    onChange={(e) => setSearchName(e.target.value)}
                />
            </div>

            {alerts.map((a) => a.alert)}
            {modal}

            <Modal isOpen={editModalOpen} toggle={handleCloseModal} className="achievement-edit-modal">
                <ModalHeader toggle={handleCloseModal}>
                    Editar Condición: {editingAchievement?.name}
                </ModalHeader>
                <ModalBody>
                    <Label for="condition-input" className="achievement-modal-label">
                        Condición (número entero):
                    </Label>
                    <Input
                        id="condition-input"
                        type="number"
                        placeholder="Ingresa el nuevo valor..."
                        value={editCondition}
                        onChange={(e) => setEditCondition(e.target.value)}
                        className="achievement-condition-input"
                    />
                </ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={handleCloseModal}>
                        Cancelar
                    </Button>
                    <Button color="success" onClick={handleSaveCondition}>
                        Guardar
                    </Button>
                </ModalFooter>
            </Modal>

            <div className="achievement-grid">
                {filteredAchievements.length === 0 ? (
                    <div className="achievement-empty">
                        <p>No hay logros disponibles</p>
                    </div>
                ) : (
                    filteredAchievements.map((achievement) => (
                        <div key={achievement.id} className="achievement-card">
                            <div className="achievement-card-header">
                                <h3 className="achievement-name">{achievement.name}</h3>
                                <span className="achievement-badge">ID: {achievement.id}</span>
                            </div>
                            <div className="achievement-card-body">
                                <p className="achievement-description">{achievement.description}</p>
                            </div>
                            <div className="achievement-card-footer">
                                <ButtonGroup>
                                    <Button
                                        size="sm"
                                        color="info"
                                        aria-label={`edit-${achievement.id}`}
                                        onClick={() => handleEdit(achievement)}
                                        className="action-btn edit-btn"
                                    >
                                        <FaEdit /> Editar
                                    </Button>
                                    <Button
                                        size="sm"
                                        color="danger"
                                        aria-label={`delete-${achievement.id}`}
                                        onClick={() => handleDelete(achievement.id)}
                                        className="action-btn delete-btn"
                                    >
                                        <FaTrash /> Borrar
                                    </Button>
                                </ButtonGroup>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}