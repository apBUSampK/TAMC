# TAMC
## SLM (Transparant) Array Monte Carlo

A program for modelling SLM diffraction using Monte Carlo integration of Huygens-Fresnel integral.

Each SLM draws cached amplitudes from randomly selected points from the previous one and so on, until the source of light
is met. It should be possible to swap any SLM without additional calculations of everything between it and the source.