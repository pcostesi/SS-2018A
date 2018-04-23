data = dlmread("harmonic0.0001", "'\t'");
t = data(:,1);
beemanPos = data(:,2);
gpco5Pos = data(:, 3);
verlet = data(:,4);
analyticPos = data(:, 5);
plot(t, beemanPos, ";beemanAprox;", t, verlet, ";verletAprox;", t, gpco5Pos, ";gpoc5;", t, analyticPos, ";analyticAprox;");