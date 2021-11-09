package dynamic_programming;

import dynamic_programming.models.Edge;
import dynamic_programming.models.Node;
import dynamic_programming.repo.NodeRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;

public class TestDP {

    NodeRepo nodeRepo;

    @Before
    public void setup() {
        nodeRepo=new NodeRepo();

        Node node1a=new Node("1a",1);
        node1a.setEdges(Arrays.asList(new Edge("2a",22.0),new Edge("2b",8.0),new Edge("2c",12.0)));
        nodeRepo.add(node1a);

        Node node2a=new Node("2a",2);
        node2a.setEdges(Arrays.asList(new Edge("3a",8.0),new Edge("3b",10.0)));
        nodeRepo.add(node2a);

        Node node2b=new Node("2b",2);
        node2b.setEdges(Arrays.asList(new Edge("3a",25.0),new Edge("3b",13.0)));
        nodeRepo.add(node2b);

        Node node2c=new Node("2c",2);
        node2c.setEdges(Arrays.asList(new Edge("3a",12.0),new Edge("3b",13.0)));
        nodeRepo.add(node2c);

        Node node3a=new Node("3a",3);
        node3a.setEdges(Collections.emptyList() );
        nodeRepo.add(node3a);

        Node node3b=new Node("3b",3);
        node3b.setEdges(Collections.emptyList() );
        nodeRepo.add(node3b);


    }

    @Test
    public void testSetup() {
        Assert.assertEquals(6,nodeRepo.nofItems());

        System.out.println(nodeRepo.get("1a"));


    }

}
