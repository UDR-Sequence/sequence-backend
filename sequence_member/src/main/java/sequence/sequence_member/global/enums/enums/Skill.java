package sequence.sequence_member.global.enums.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sequence.sequence_member.global.exception.CanNotFindResourceException;

@Getter
@RequiredArgsConstructor
public enum Skill {
    ADOBE_ILLUSTRATION("Adobe illustration"),
    ADOBE_PHOTOSHOP("Adobe Photoshop"),
    ADOBE_INDESIGN("Adobe InDesign"),
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    FIGMA("Figma"),
    SPRING("Spring"),
    DOCKER("Docker"),
    NODE_JS("Node.js"),
    REACT("React"),
    VUE_JS("Vue.js"),
    NEXT_JS("Next.js"),
    DJANGO("Django"),
    AWS("AWS"),
    REACT_NATIVE("React Native"),
    FLUTTER("Flutter"),
    ANGULAR("Angular"),
    ;
    //to-do 추후 추가 예정

    private final String name;

    public static Skill fromString(String name) {
        for (Skill skill : Skill.values()) {
            if (skill.getName().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        throw new CanNotFindResourceException("존재하지 않는 스킬입니다. " + name);
    }

}
