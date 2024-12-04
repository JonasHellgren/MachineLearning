package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ExperiencesInfo {

    List<ExperienceLunar> experiences;

    public static ExperiencesInfo of(List<ExperienceLunar> experiences) {
        return new ExperiencesInfo(experiences);
    }

    public int nExperiences() {
        return experiences.size();
    }

    public ExperienceLunar endExperience() {
        return experiences.get(nExperiences() - 1);
    }


}
