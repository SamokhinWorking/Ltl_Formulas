package ltlgen.filters;

public class TemporalFilter extends Filter {

    @Override
    public boolean accepts(String formula, int complexity) {
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (c == 'X' || c == 'F' || c == 'U' || c == 'V') {
                return true;
            }
        }
        return false;
    }
}
