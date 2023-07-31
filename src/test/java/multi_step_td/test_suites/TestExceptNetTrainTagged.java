package multi_step_td.test_suites;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"multi_step_td.charge","multi_step_td.fork","multi_step_td.maze"})
@ExcludeTags("nettrain")
public class TestExceptNetTrainTagged {
}
