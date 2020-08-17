package ch.swisssmp.utils;

import java.util.List;
import java.util.function.Consumer;

public class Procedure {

    private final Step[] steps;

    private Procedure(Step[] steps){
        this.steps = steps;
    }

    private void initialize(){
        for(int i = 0; i < steps.length-1; i++){
            Step current = steps[i];
            Step next = steps[i+1];
            current.onFinish(next::run);
        }
    }

    private void run(){
        steps[0].run();
    }

    public static void run(List<Consumer<Runnable>> callbacks){
        Step[] steps = new Step[callbacks.size()];
        for(int i = 0; i < callbacks.size(); i++){
            steps[i] = new Step(callbacks.get(i));
        }
        Procedure result = new Procedure(steps);
        result.initialize();
        result.run();
    }

    private static class Step{
        private final Consumer<Runnable> step;
        private Runnable callback;

        public Step(Consumer<Runnable> step){
            this.step = step;
        }

        public void onFinish(Runnable callback){
            this.callback = callback;
        }

        public void run(){
            this.step.accept(callback!=null ? callback : ()->{});
        }
    }
}
