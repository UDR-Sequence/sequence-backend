package sequence.sequence_member.global.enums.enums;

public enum ProjectRole {
    UX_UI_DESIGN,
    BX_DESIGN,
    FRONT_END,
    BACK_END,
    PM;

    public static ProjectRole stringToRole(String role) {
        try {
            return ProjectRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for role: " + role, e);
        }
    }
}
