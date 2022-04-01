package com.sas.betterPlants.customLogic;

import java.util.Random;

public class EvolutionaryPlant {

    private double growthRate;
    private double dropRate;
    private ENVIRONMENT preferredEnvironment;

    private final Random rand;

    public EvolutionaryPlant() {
        this.rand = new Random();
        init();
    }

    public EvolutionaryPlant(int seed) {
        this.rand = new Random(seed);
        init();
    }

    public EvolutionaryPlant(double growthRate, double dropRate, ENVIRONMENT preferredEnvironment, Random rand) {
        this.rand = rand;

        this.growthRate = growthRate;
        this.dropRate = dropRate;
        this.preferredEnvironment = preferredEnvironment;
    }

    public void init() {
        this.growthRate = rand.nextDouble();
        this.dropRate = rand.nextDouble();
        this.preferredEnvironment = ENVIRONMENT.values()[rand.nextInt(ENVIRONMENT.values().length)];
    }

    public static EvolutionaryPlant breed(EvolutionaryPlant parent1, EvolutionaryPlant parent2) {
        EvolutionaryPlant child = new EvolutionaryPlant();

        double parentGrowthRate = (parent1.growthRate + parent2.growthRate) / 3;
        double parentDropRate = (parent1.dropRate + parent2.dropRate) / 3;
        ENVIRONMENT parentEnvironment = parent1.preferredEnvironment.equals(parent2.preferredEnvironment) ? parent1.preferredEnvironment : child.preferredEnvironment;

        child.growthRate = parentGrowthRate + (child.growthRate / 3);
        child.dropRate = parentDropRate + (child.dropRate / 3);
        child.preferredEnvironment = parentEnvironment;

        return child;
    }

    public double getGrowthRate() {
        return growthRate;
    }

    public double getDropRate() {
        return dropRate;
    }

    public ENVIRONMENT getPreferredEnvironment() {
        return preferredEnvironment;
    }

}
