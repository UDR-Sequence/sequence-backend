package sequence.sequence_member.global.enums.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sequence.sequence_member.global.exception.CanNotFindResourceException;

@Getter
@RequiredArgsConstructor
public enum Skill {
    JAVASCRIPT("JavaScript"),
    TYPESCRIPT("TypeScript"),
    REACT("React.js"),
    VUE_JS("Vue.js"),
    SVELTE("Svelte"),

    ANGULAR("Angular.js"),

    FIGMA("Figma"),

    SPRINGBOOT("SpringBoot"),
    NODE_JS("Node.js"),
    NEXT_JS("Next.js"),
    DJANGO("Django"),

    DOCKER("Docker"),
    AWS("AWS"),
    NGINX("Nginx"),
    KUBERNETES("Kubernetes"),
    GIT("git"),
    JENKINS("Jenkins"),
    GITHUB_ACTIONS("Github Actions"),

    FLUTTER("Flutter"),
    REACT_NATIVE("React Native"),
    SWIFT("Swift"),
    KOTLIN("Kotlin"),
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

    public static String fromSkillEnum(Skill name){
        for(Skill skill : Skill.values()){
            if(skill.toString().equals(name.toString())){
                return skill.getName();
            }
        }
        throw new CanNotFindResourceException("존재하지 않는 스킬입니다. " + name.toString());

    }

}
