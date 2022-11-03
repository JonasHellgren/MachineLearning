package black_jack.models;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class ReturnsForEpisode {

    List<ReturnItem> returns;

    //Map<StateObserved,Double> stateReturnMap;


    public ReturnsForEpisode() {
        returns = new ArrayList<>();
    }

    public int nofItems() {
        return returns.size();
    }

    public void clear() {
        returns.clear();
    }

    public void add(StateObserved state, Double returnValue) {
        returns.add(new ReturnItem(state, returnValue));
    }

    public ReturnItem getItem(Integer index) {
        if (index < 0 || index >= nofItems()) {
            throw new IllegalArgumentException();
        }
        return returns.get(index);
    }

    public void appendReturns(Episode episode) {
        double G=0;
        for (int i = episode.nofItems()-1; i >=0 ; i--) {
            EpisodeItem ei= episode.getItem(i);
            G=G+1*ei.reward;
            if (!episode.isStatePresentBeforeTimeStep(ei.state,i)) {
                this.add(ei.state,G);  //todo, first visit flag
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ReturnItem ri : returns) {
            sb.append(ri.toString());
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

}
