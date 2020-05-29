package ltlgen.filters;

import automaton.Automaton;
import verifier.Verifier;

public class VerifiedFilter extends Filter {

    private static final Verifier verifier = new Verifier(Automaton.automaton);

    @Override
    public boolean accepts(String formula, int complexity) {
        return verifier.verify(formula).verified > 0;
    }
}
