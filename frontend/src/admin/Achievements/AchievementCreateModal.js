import React, { useState, useRef, useEffect } from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Input, Label, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from "reactstrap";
import tokenService from "../../services/token.service";
import trofeo from "../../static/images/trofeo.png";

const DEFAULT_ACHIEVEMENT_IMAGE = trofeo;

export default function AchievementCreateModal({ isOpen, toggle, onSave, statistics }) {
    const [newAchievement, setNewAchievement] = useState({
        name: "",
        description: "",
        statisticName: "",
        valor: ""
    });
    const [dropdownOpen, setDropdownOpen] = useState(false);
    const [achievementImage, setAchievementImage] = useState(DEFAULT_ACHIEVEMENT_IMAGE);
    const imageInputRef = useRef(null);

    useEffect(() => {
        setAchievementImage(DEFAULT_ACHIEVEMENT_IMAGE);
    }, [isOpen]);

    const handleClose = () => {
        setNewAchievement({
            name: "",
            description: "",
            statisticName: "",
            valor: ""
        });
        setDropdownOpen(false);
        setAchievementImage(DEFAULT_ACHIEVEMENT_IMAGE);
        toggle();
    };

    const handleSave = () => {
        onSave(newAchievement, achievementImage);
        handleClose();
    };

    const handleChangeImage = () => {
        imageInputRef.current.click();
    };

    const handleFileChange = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // Validación del tipo y tamaño de archivo
        if (!file.type.startsWith('image/')) {
            alert('Por favor, selecciona un archivo de imagen válido.');
            return;
        }
        if (file.size > 5 * 1024 * 1024) { // 5MB limit
            alert('El archivo es demasiado grande. Máximo 5MB.');
            return;
        }

        // Crear una URL local para previsualización
        const reader = new FileReader();
        reader.onload = (e) => {
            setAchievementImage(e.target.result);
            setNewAchievement({ ...newAchievement, imageFile: file });
        };
        reader.readAsDataURL(file);
    };

    return (
        <Modal isOpen={isOpen} toggle={handleClose} className="achievement-edit-modal">
            <ModalHeader toggle={handleClose}>
                Crear Nuevo Logro
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
                        {statistics.map((statistic) => (
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
                <Button color="secondary" onClick={handleClose}>
                    Cancelar
                </Button>
                <Button color="success" onClick={handleSave}>
                    Crear
                </Button>
            </ModalFooter>
        </Modal>
    );
}
