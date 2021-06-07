(*1.1*)
let rec pow (x: int) (y: int) =
  if y = 0 then
    1
  else
    x * pow x (y-1);;

let rec float_pow (x: float) (y: int) = 
  if y = 0 then
    1.0
  else 
    x *. float_pow x (y-1);;

(*1.2*)
let rec compress (l: 'a list) : 'a list = 
  match l with
    | [] -> []
    | [x] as a -> a
    | a::(b::t as tail) -> if a = b then compress tail else a::compress tail;;

(*1.3*)
let rec remove_if  (l: 'a list) p = 
  match l with
    | [] -> []
    | a::b -> if p a then remove_if b p else a::remove_if b p;;

(*1.4*)   
let rec traverse l i = 
    match l with 
    | [] -> []
    | a::b -> if i = 0 then a::b else traverse b (i-1);;

let rec limit l j =
  match l with
  | [] -> []
  |  a::b -> if j = 0 then [] else a:: limit b (j-1);;

let rec slice l i j =
  if i > j 
    then []
  else
    limit (traverse l i) (j - i);;

(*1.5*)
let rec predicate_evaluation p l =
  match l with
  | [] -> []
  | a::b -> if p a then a::predicate_evaluation p b else predicate_evaluation p b

let rec compare_list lst sublst =
  match lst with
  | [] -> sublst
  | a::b -> compare_list (b) (remove_if (sublst) ((fun x y-> x = y) a))

let rec equiv_helper p x e =
  match e with
  | [] -> [x]
  | a::b -> x::(equivs p e)
and equivs p l =
  match l with
  | [] -> [[]]
  | a::b -> equiv_helper (p) (predicate_evaluation (p a) (l)) (compare_list (predicate_evaluation (p a) (l)) (b))

(*1.6cannot figure out*)
(* let rec is_divisible x d =
  (x >= d * d) && (x mod d = 0 || not(is_divisible (x) (d+1)));;

let rec goldbach_helper x n =
  if not(is_divisible x 2) && not(is_divisible (x-n) (2)) then (x, x-n) else goldbach_helper (x) (n+1);;

let rec goldbachpair x = 
  goldbach_helper x 2;; *)
  
(*1.7*)
let rec equiv_on f g lst =
  match lst with
  | [] -> true
  | a::b -> if (f a = g a) then equiv_on (f) (g) (b) else false;;

(*1.8*)
let rec pairwisefilter cmp lst =
  match lst with
  | [] -> []
  | a::b::c -> (cmp a b)::pairwisefilter cmp c
  | a::[] -> [a];;

(*1.9*)
let rec polynomial l = fun x->
  match l with
  | [] -> 0
  | (a,b)::c ->  a*(pow x b) + (polynomial c) x;;

(*1.10*)
let rec find_subsets e l =
  match l with
  | [] -> []
  | a::b -> (e a)::(find_subsets e b);;

let rec powerset l = 
  match l with
  | [] -> [[]]
  | a::b -> (powerset b)@(find_subsets (fun e -> a::e) (powerset b));;
