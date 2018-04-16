data = dlmread("armonicout.dat", "'\t'");
t = data(:,1);
beemanPos = data(:,2);
analyticPos = data(:, 3);
plot(t, beemanPos, ";beemanAprox;", t, analyticPos, ";analyticAprox;");