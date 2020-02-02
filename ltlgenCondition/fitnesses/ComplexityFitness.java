package ltlgenCondition.fitnesses;





public class ComplexityFitness extends SingleFitness {
    @Override
    public double getCondFitness(String formula, int complexity) {
        double result = 1 / (1.0 + complexity);
        return result < threshold ? -1 : result;
    }
}
