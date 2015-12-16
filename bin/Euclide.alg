
co Euclid's algorithm co

begin
  read(a) ;
  read(b) ;
  if a>2 then
   a := a+1 
  else 
   b := 1+a
  fi;
  
  while b /= 0 and a>(3+4+5) or c < 2 and b = c do
    c := b ;
    while a >= b do     co computation of modulo co
      a := a-b
    od ;
    b := a ;
    a := c
  od ;
  print(a);
  
  for a from 0 by a+1 to 10 do 
	b := b +1
  od
  
end