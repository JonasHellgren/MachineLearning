package safe_rl.persistance.trade_environment;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ElPriceXlsRead {
    ElPriceRepo repo;


     public void readDataFromFile(PathAndFile file, ElType type)  {

     }

    public void clear() {
         repo.clear();
    }


}
