import React from "react";
import { Button, ButtonGroup } from "reactstrap";
import { FaEdit, FaTrash } from "react-icons/fa";
import trofeo from "../../static/images/trofeo.png";

export default function AchievementCard({ achievement, onEdit, onDelete, editingAchievementValue }) {
    return (
        <div 
            key={achievement.id} 
            className="achievement-card"
            style={{ '--achievement-image': `url(${trofeo})` }}
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
                    <strong>Valor:</strong> {editingAchievementValue || achievement.valor || "N/A"}
                </div>
                <ButtonGroup>
                    <Button
                        size="sm"
                        color="info"
                        aria-label={`edit-${achievement.id}`}
                        onClick={() => onEdit(achievement)}
                        className="action-btn edit-btn"
                    >
                        <FaEdit /> Editar
                    </Button>
                    <Button
                        size="sm"
                        color="danger"
                        aria-label={`delete-${achievement.id}`}
                        onClick={() => onDelete(achievement.id)}
                        className="action-btn delete-btn"
                    >
                        <FaTrash /> Borrar
                    </Button>
                </ButtonGroup>
            </div>
        </div>
    );
}
