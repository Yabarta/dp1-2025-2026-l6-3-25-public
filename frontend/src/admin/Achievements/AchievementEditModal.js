import React, { useState, useRef, useEffect } from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Input, Label, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from "reactstrap";
import tokenService from "../../services/token.service";
import trofeo from "../../static/images/trofeo.png";

const DEFAULT_ACHIEVEMENT_IMAGE = trofeo;

export default function AchievementEditModal({ isOpen, toggle, achievement, onSave, statistics }) {
    const [editingAchievement, setEditingAchievement] = useState(achievement);
    const [editCondition, setEditCondition] = useState(achievement?.valor?.toString() || "");
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [message, setMessage] = useState(null);
    const [messageVisible, setMessageVisible] = useState(false);
    const [achievementImage, setAchievementImage] = useState(DEFAULT_ACHIEVEMENT_IMAGE);
    const imageInputRef = useRef(null);

    React.useEffect(() => {
        if (achievement) {
            setEditingAchievement(achievement);
            setEditCondition(achievement.valor?.toString() || "");
        }
    }, [achievement, isOpen]);

    useEffect(() => {
        setAchievementImage(editingAchievement?.image || DEFAULT_ACHIEVEMENT_IMAGE);
    }, [editingAchievement]);

    const handleClose = () => {
        toggle();
    };

    const handleSave = async () => {
        const jwt = tokenService.getLocalAccessToken();
        const formData = new FormData();
        formData.append('name', editingAchievement.name);
        formData.append('description', editingAchievement.description);
        formData.append('valor', editCondition);
        formData.append('statisticName', editingAchievement.statisticName);

        try {
            const response = await fetch(`/api/v1/achievements/${editingAchievement.id}`, {
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
                setMessageVisible(true);
                return;
            }

            const updatedAchievement = await response.json();
            setEditingAchievement(updatedAchievement);
            setMessage('Logro actualizado exitosamente.');
            setMessageVisible(true);
            setTimeout(() => {
                handleClose();
                // Notificar al padre que se guardó
                onSave(updatedAchievement, editCondition);
            }, 1000);
        } catch (error) {
            setMessage('Error al actualizar el logro. Inténtalo de nuevo.');
            setMessageVisible(true);
        }
    };

    const handleChangeImage = () => {
        imageInputRef.current.click();
    };

    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // Validación del tipo y tamaño de archivo
        if (!file.type.startsWith('image/')) {
            setMessage('Por favor, selecciona un archivo de imagen válido.');
            setMessageVisible(true);
            return;
        }
        if (file.size > 5 * 1024 * 1024) { // 5MB limit
            setMessage('El archivo es demasiado grande. Máximo 5MB.');
            setMessageVisible(true);
            return;
        }

        const jwt = tokenService.getLocalAccessToken();
        const formData = new FormData();
        formData.append('image', file);

        try {
            const response = await fetch(`/api/v1/achievements/${editingAchievement.id}`, {
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
                setMessageVisible(true);
                return;
            }

            const updatedAchievement = await response.json();
            setEditingAchievement(updatedAchievement);
            setAchievementImage(updatedAchievement.image || DEFAULT_ACHIEVEMENT_IMAGE);
            setMessage('Imagen actualizada exitosamente.');
            setMessageVisible(true);
        } catch (error) {
            console.error('Error:', error);
            setMessage('Error al subir la imagen. Inténtalo de nuevo.');
            setMessageVisible(true);
        }
    };

    if (!editingAchievement) return null;

    return (
        <Modal isOpen={isOpen} toggle={handleClose} className="achievement-edit-modal">
            <ModalHeader toggle={handleClose}>
                Editando Logro: {editingAchievement.name}
            </ModalHeader>
            <ModalBody>
                <Label for="condition-input" className="achievement-modal-label">
                    Imagen del Logro:
                </Label>
                <div className="achievement-image-preview-container">
                    <img 
                        src={achievementImage} 
                        onClick={handleChangeImage}
                        alt="Logro" 
                        className="achievement-image-preview"
                    />
                </div>
                <Button 
                    color="info" 
                    onClick={handleChangeImage}
                    className="achievement-change-image-btn"
                >
                    Cambiar Imagen
                </Button>
                <input 
                    type="file" 
                    ref={imageInputRef} 
                    onChange={handleFileChange} 
                    className="hiddenFileInput" 
                    accept="image/*" 
                    style={{ display: 'none' }}
                />
                <Label for="condition-input" className="achievement-modal-label">
                    Estadistica de la que depende:
                </Label>
                <Dropdown isOpen={dropdownOpen} toggle={() => setDropdownOpen(!dropdownOpen)}>
                    <DropdownToggle caret className="achievement-condition-dropdown">
                        {editingAchievement.statisticName || "Selecciona una estadística"}
                    </DropdownToggle>
                    <DropdownMenu>
                        {statistics.map((statistic) => (
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
                <Button color="secondary" onClick={handleClose}>
                    Cancelar
                </Button>
                <Button color="success" onClick={handleSave}>
                    Guardar
                </Button>
            </ModalFooter>
        </Modal>
    );
}
