# AstraPortia
Demo project fo Stargate Hackathon

## DSBulk

### Planetary systems composite
```
dsbulk load -url 1_datasets/PSCompPars_2020.09.10_11.00.24.csv -k stargate -t planetary_systems_composite -b "secure-connect-clavis.zip" -u gateuser -p gatepassword -header false -m '0=pl_name,1=hostname,2=sy_snum,3=sy_pnum,4=discoverymethod,5=disc_year,6=disc_facility'
```
