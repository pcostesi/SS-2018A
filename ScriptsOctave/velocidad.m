data = dlmread("velocity.out", "'\t'");
t = data(:,1);
velocity = data(:,2);
plot(t, velocity, ";|V|;");
title ("Modulo Velocidad");
xlabel ("Tiempo (s)");
ylabel ("Modulo Velocidad(Km/s)");