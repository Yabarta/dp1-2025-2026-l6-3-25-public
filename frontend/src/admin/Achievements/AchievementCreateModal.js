import React, { useState } from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Input, Label, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from "reactstrap";

export default function AchievementCreateModal({ isOpen, toggle, onSave, statistics }) {
    const [newAchievement, setNewAchievement] = useState({
        name: "",
        description: "",
        statisticName: "",
        valor: ""
    });
    const [dropdownOpen, setDropdownOpen] = useState(false);

    const handleClose = () => {
        setNewAchievement({
            name: "",
            description: "",
            statisticName: "",
            valor: ""
        });
        setDropdownOpen(false);
        toggle();
    };

    const handleSave = () => {
        onSave(newAchievement);
        handleClose();
    };

    return (
        <Modal isOpen={isOpen} toggle={handleClose} className="achievement-edit-modal">
            <ModalHeader toggle={handleClose}>
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
