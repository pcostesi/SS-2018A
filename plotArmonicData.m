data = dlmread("armonicout.dat", "'\t'");
t = data(:,1);
beemanPos = data(:,2);
gpco5Pos = data(:, 3);
analyticPos = data(:, 4);
plot(t, beemanPos, ";beemanAprox;", t, gpco5Pos, ";GPCo5;", t, analyticPos, ";analyticAprox;");