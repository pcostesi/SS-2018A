data = dlmread("armonicout.dat", "'\t'");
t = data(:,1);
beemanPos = data(:,2);
verlet = data(:,3);
analyticPos = data(:, 4);
plot(t, beemanPos, ";beemanAprox;", t, verlet, ";verletAprox;", t, analyticPos, ";analyticAprox;");