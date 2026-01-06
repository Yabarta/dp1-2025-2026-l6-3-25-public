import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, ButtonGroup, Modal, ModalHeader, ModalBody, ModalFooter, Input, Label, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from "reactstrap";
import { FaEdit, FaTrash, FaTrophy } from "react-icons/fa";
import tokenService from "../../services/token.service";
import useFetchState from "../../util/useFetchState";
import deleteFromList from "../../util/deleteFromList";
import getErrorModal from "../../util/getErrorModal";
import trofeo from "../../static/images/trofeo.png";
import "../../static/css/admin/achievementListAdmin.css";

const jwt = tokenService.getLocalAccessToken();

export default function AchievementListAdmin() {
    const Statistics = ["games_played", "games_won", "sarcines_created", "bacterias_created", "time_played"];
    const DEFAULT_PROFILE_PIC = trofeo;
    const navigate = useNavigate();
    const [message, setMessage] = useState(null);
    const [visible, setVisible] = useState(false);
    const [searchName, setSearchName] = useState("");
    const [alerts, setAlerts] = useState([]);
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [editingAchievement, setEditingAchievement] = useState(null);
    const [editCondition, setEditCondition] = useState("");
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [newAchievement, setNewAchievement] = useState({
        name: "",
        description: "",
        statisticName: "",
        valor: ""
    });
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
console.log(achievements);
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
            valor: conditionValue
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

    const handleOpenCreateModal = () => {
        setCreateModalOpen(true);
    };

    const handleCloseCreateModal = () => {
        setCreateModalOpen(false);
        setNewAchievement({
            name: "",
            description: "",
            statisticName: "",
            valor: ""
        });
    };

    const handleSaveNewAchievement = () => {
        if (!newAchievement.name || !newAchievement.description || !newAchievement.statisticName || !newAchievement.valor) {
            setMessage("Por favor completa todos los campos");
            setVisible(true);
            return;
        }

        const valorValue = parseInt(newAchievement.valor, 10);
        if (isNaN(valorValue)) {
            setMessage("El valor debe ser un número válido");
            setVisible(true);
            return;
        }

        const achievementData = {
            name: newAchievement.name,
            description: newAchievement.description,
            image: "imagelin.png",
            statisticName: newAchievement.statisticName,
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
                    handleCloseCreateModal();
                    setMessage("Logro creado exitosamente");
                    setVisible(true);
                }
            })
            .catch((err) => {
                setMessage("Error al crear el logro");
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

    const modal = getErrorModal(setVisible, visible, message);

    return (
        <div className="achievement-admin-container">
            <div className="achievement-header">
                <div className="achievement-title-section">
                    <FaTrophy className="achievement-icon" />
                    <h1 className="achievement-title">Gestionar Logros</h1>
                </div>
                <div className="achievement-header-controls">
                    <input
                        type="text"
                        placeholder="Buscar logro..."
                        className="achievement-search"
                        value={searchName}
                        onChange={(e) => setSearchName(e.target.value)}
                    />
                    <Button 
                        color="success" 
                        onClick={handleOpenCreateModal}
                        className="achievement-create-btn"
                    >
                        + Crear Logro
                    </Button>
                </div>
            </div>

            {alerts.map((a) => a.alert)}
            {modal}

            <Modal isOpen={createModalOpen} toggle={handleCloseCreateModal} className="achievement-edit-modal">
                <ModalHeader toggle={handleCloseCreateModal}>
                    Crear Nuevo Logro
                </ModalHeader>
                <ModalBody>
                    <Label for="new-name" className="achievement-modal-label">
                        Nombre:
                    </Label>
                    <Input
                        id="new-name"
                        type="text"
                        placeholder="Ingresa el nombre del logro..."
                        value={newAchievement.name}
                        onChange={(e) => setNewAchievement({ ...newAchievement, name: e.target.value })}
                        className="achievement-condition-input"
                    />
                    <Label for="new-description" className="achievement-modal-label">
                        Descripción:
                    </Label>
                    <Input
                        id="new-description"
                        type="textarea"
                        placeholder="Ingresa la descripción del logro..."
                        value={newAchievement.description}
                        onChange={(e) => setNewAchievement({ ...newAchievement, description: e.target.value })}
                        className="achievement-condition-input"
                    />
                    <Label for="new-statistic" className="achievement-modal-label">
                        Estadistica de la que depende:
                    </Label>
                    <Dropdown isOpen={dropdownOpen} toggle={() => setDropdownOpen(!dropdownOpen)}>
                        <DropdownToggle caret className="achievement-condition-dropdown">
                            {newAchievement.statisticName || "Selecciona una estadística"}
                        </DropdownToggle>
                        <DropdownMenu>
                            {Statistics.map((statistic) => (
                                <DropdownItem
                                    key={statistic}
                                    onClick={() => {
                                        setNewAchievement({ ...newAchievement, statisticName: statistic });
                                        setDropdownOpen(false);
                                    }}
                                >
                                    {statistic}
                                </DropdownItem>
                            ))}
                        </DropdownMenu>
                    </Dropdown>
                    <Label for="new-valor" className="achievement-modal-label">
                        Valor:
                    </Label>
                    <Input
                        id="new-valor"
                        type="number"
                        placeholder="Ingresa el valor..."
                        value={newAchievement.valor}
                        onChange={(e) => setNewAchievement({ ...newAchievement, valor: e.target.value })}
                        className="achievement-condition-input"
                    />
                </ModalBody>
                <ModalFooter>
                    <Button color="secondary" onClick={handleCloseCreateModal}>
                        Cancelar
                    </Button>
                    <Button color="success" onClick={handleSaveNewAchievement}>
                        Crear
                    </Button>
                </ModalFooter>
            </Modal>

            <Modal isOpen={editModalOpen} toggle={handleCloseModal} className="achievement-edit-modal">
                <ModalHeader toggle={handleCloseModal}>
                    Editando Logro: {editingAchievement?.name}
                </ModalHeader>
                <ModalBody>
                    <Label for="condition-input" className="achievement-modal-label">
                        Estadistica de la que depende:
                    </Label>
                    <Dropdown isOpen={dropdownOpen} toggle={() => setDropdownOpen(!dropdownOpen)}>
                        <DropdownToggle caret className="achievement-condition-dropdown">
                            {editingAchievement?.statisticName || "Selecciona una estadística"}
                        </DropdownToggle>
                        <DropdownMenu>
                            {Statistics.map((statistic) => (
                                <DropdownItem
                                    key={statistic}
                                    onClick={() => {
                                        setEditingAchievement({
                                            ...editingAchievement,
                                            statisticName: statistic,
                                        });
                                        setDropdownOpen(false);
                                    }}
                                >
                                    {statistic}
                                </DropdownItem>
                            ))}
                        </DropdownMenu>
                    </Dropdown>
                    <Label for="condition-input" className="achievement-modal-label">
                        Valor:
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
                        <div 
                            key={achievement.id} 
                            className="achievement-card"
                            style={{ '--achievement-image': `url(${DEFAULT_PROFILE_PIC})` }}
                        >
                            <div className="achievement-card-header">
                                <h3 className="achievement-name">{achievement.name}</h3>
                                <span className="achievement-badge">ID: {achievement.id}</span>
                            </div>
                            <div className="achievement-card-body">
                                <p className="achievement-description">{achievement.description}</p>
                            </div>
                            <div className="achievement-card-footer">
                                <div className="achievement-value">
                                    <strong>Valor:</strong> {editingAchievement?.valor || achievement.valor || "N/A"}
                                </div>
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