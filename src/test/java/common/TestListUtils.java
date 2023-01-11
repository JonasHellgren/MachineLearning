package common;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestListUtils {

    @AllArgsConstructor
    static
    class Dummy {
        Integer a;
        String b;
    }

    @SneakyThrows
    @Test
    public void  testGetFieldByStringName() {

        Dummy d= new Dummy(1, "");
        Field field = d.getClass().getDeclaredField("a");
        int value = (int) field.get(d);
        System.out.println("value = " + value);
        Assert.assertEquals(1,value);

    }

    @SneakyThrows
    @Test public void getListOfField() {

        List<Dummy> list=new ArrayList<>();
        list.add(new Dummy(1, "a"));
        list.add(new Dummy(2, "b"));

        List<Integer> intList=ListUtils.getListOfField(list,"a");
        List<String> stringList=ListUtils.getListOfField(list,"b");
        System.out.println("intList = " + intList);
        System.out.println("stringList = " + stringList);

    }

    @SneakyThrows
    @Test public void getListOfFieldEmpty() {

        List<Dummy> list=new ArrayList<>();

        List<Integer> intList=ListUtils.getListOfField(list,"a");
        List<String> stringList=ListUtils.getListOfField(list,"b");
        System.out.println("intList = " + intList);
        System.out.println("stringList = " + stringList);

    }

}
