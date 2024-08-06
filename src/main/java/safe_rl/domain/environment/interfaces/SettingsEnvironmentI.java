package safe_rl.domain.environment.interfaces;

import com.google.common.collect.Range;

public interface SettingsEnvironmentI {

    double timeEnd();
    Range<Double> socRange();
    double dt();
}
