import React, { useState } from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Input, Label, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from "reactstrap";

export default function AchievementEditModal({ isOpen, toggle, achievement, onSave, statistics }) {
    const [editingAchievement, setEditingAchievement] = useState(achievement);
    const [editCondition, setEditCondition] = useState(achievement?.valor?.toString() || "");
    const [dropdownOpen, setDropdownOpen] = useState(false);

    React.useEffect(() => {
        if (achievement) {
            setEditingAchievement(achievement);
            setEditCondition(achievement.valor?.toString() || "");
        }
    }, [achievement, isOpen]);

    const handleClose = () => {
        toggle();
    };

    const handleSave = () => {
        onSave(editingAchievement, editCondition);
        handleClose();
    };

    if (!editingAchievement) return null;

    return (
        <Modal isOpen={isOpen} toggle={handleClose} className="achievement-edit-modal">
            <ModalHeader toggle={handleClose}>
                Editando Logro: {editingAchievement.name}
            </ModalHeader>
            <ModalBody>
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
