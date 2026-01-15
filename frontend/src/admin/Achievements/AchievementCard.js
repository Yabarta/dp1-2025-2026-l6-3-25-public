import React from "react";
import { Button, ButtonGroup } from "reactstrap";
import { FaEdit, FaTrash } from "react-icons/fa";
import trofeo from "../../static/images/trofeo.png";
import AchievementProgressBar from "./AchievementProgressBar";

export default function AchievementCard({ achievement, onEdit, onDelete, isEditing, isAdmin, playerStats }) {
    const getImageUrl = (image) => {
        if (!image) return trofeo;
        if (image.startsWith('/')) {
            return `url(${image})`;
        }
        return `url(${trofeo})`;
    };

    return (
        <div 
            key={achievement.id} 
            className="achievement-card"
            style={{ '--achievement-image': getImageUrl(achievement.image) }}
        >   
            <div className="achievement-card-header">
                <h3 className="achievement-name">{achievement.name}</h3>
                {
                    isAdmin
                    &&                
                    <span className="achievement-badge">ID: {achievement.id}</span>
                }

            </div>
            <div className="achievement-card-body">
                <p className="achievement-description">{achievement.description}</p>
            </div>
            {
                isAdmin
                && 
                <div className="achievement-card-footer">
                <div className="achievement-value">
                    <strong>Valor:</strong> {achievement.valor || "N/A"}
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
            }
            {
                !isAdmin
                &&
                <AchievementProgressBar
                    achievement={achievement}
                    playerStats={playerStats}
                />
            }
        </div>
    );
}
