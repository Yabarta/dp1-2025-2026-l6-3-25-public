import React from "react";
import AchievementCard from "./AchievementCard";

export default function AchievementGrid({ achievements, editingAchievement, onEdit, onDelete }) {
    return (
        <div className="achievement-grid">
            {achievements.length === 0 ? (
                <div className="achievement-empty">
                    <p>No hay logros disponibles</p>
                </div>
            ) : (
                achievements.map((achievement) => (
                    <AchievementCard
                        key={achievement.id}
                        achievement={achievement}
                        onEdit={onEdit}
                        onDelete={onDelete}
                        isEditing={editingAchievement?.id === achievement.id}
                    />
                ))
            )}
        </div>
    );
}
