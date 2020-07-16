package io.github.dreamylost.service;

import io.github.dreamylost.model.Droid;
import io.github.dreamylost.model.Episode;
import io.github.dreamylost.model.Human;

import java.util.List;

/**
 * @author liguobin@growingio.com
 * @version 1.0, 2020/7/16
 */
public class DroidResolverImplMain {

    public static void main(String[] args) throws Exception {
        System.out.println("=======get droid id 2001=========");
        QueryResolverImpl droidResolver = new QueryResolverImpl();
        Droid d = droidResolver.droid("2001");
        System.out.println(d);

        System.out.println("=======get humans all=======");
        List<Human> hums = droidResolver.humans();
        for (Human h : hums) {
            System.out.println(h);
        }

        System.out.println("=======get human id 1002=======");
        Human hum = droidResolver.human("1002");
        System.out.println(hum);


        System.out.println("=======get hero Episode.EMPIRE=======");
        io.github.dreamylost.model.Character character = droidResolver.hero(Episode.EMPIRE);
        System.out.println(character);


    }

}
