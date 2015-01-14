package com.mcdr.corruption.drop;

import java.util.ArrayList;
import java.util.List;

public class Roll {
    private List<Drop> drops = new ArrayList<Drop>();

    public void addDrop(Drop drop) {
        drops.add(drop);
    }

    public List<Drop> getDrops() {
        return drops;
    }
}
