
co Euclid's algorithm co

begin
  read(a) ;
  read(b) ;
  if a>2 then
   a := a+1 
  else 
   b := 1+a
  fi;
  
  while b /= 0 and a>3 do
    c := b ;
    while a >= b do     co computation of modulo co
      a := a-b
    od ;
    b := a ;
    a := c
  od ;
  print(a)
end