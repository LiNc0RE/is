% Die Schnittstelle umfasst
%   start_description   ;Beschreibung des Startzustands
%   start_node          ;Test, ob es sich um einen Startknoten handelt
%   goal_node           ;Test, ob es sich um einen Zielknoten handelt
%   state_member        ;Test, ob eine Zustandsbeschreibung in einer Liste
%                        von Zustandsbeschreibungen enthalten ist
%   expand              ;Berechnung der Kind-Zustandsbeschreibungen
%   eval-path           ;Bewertung eines Pfades
%
%
%   (Action, State, Value)


start_description([
  block(block1),
  block(block2),
  block(block3),
  block(block4),  %mit Block4
  on(table,block2),
  on(table,block3),
  on(block2,block1),
  on(table,block4), %mit Block4
  clear(block1),
  clear(block3),
  clear(block4), %mit Block4
  handempty
  ]).

goal_description([
  block(block1),
  block(block2),
  block(block3),
  block(block4), %mit Block4
  on(block4,block2), %mit Block4
  on(table,block3),
  on(table,block1),
  on(block1,block4), %mit Block4
%  on(block1,block2), %ohne Block4
  clear(block3),
  clear(block2),
  handempty
  ]).


is_on(on(_, _)).
clear(_).
on_table(on(table, _)).

% false_under(Block, CurrentState, IntersectionOfGoalAndCurrentState, Accu, Result)
% Returns list of blocks in wrong position, under block 'Block'.
false_under(table, _State, _Intersection, Result, Result).
false_under(Block, _State, _Goal, Result, Result) :- member(table, Block), !.
false_under(Block, State, Goal, Result, Result) :- member(on(Under, Block), State), member(on(Under, Block), Goal), !.
false_under(Block, State, Goal, Accu, Result) :- member(on(Under, Block), State), false_under(Under, State, Goal, [Under | Accu], Result).

% on(block4, block2)

%under(HigherBlock, State) :- findall(
all_false_under(_State,[],_Goal,Result,Result).
all_false_under(State,[on(X,_Y)|RestIntersection],Goal,Accu,Result):- %writeln("before blabla"),
	false_under(X,State,Goal,[],ResultFU),
	write("false under: "), writeln(ResultFU),
								    %append(ResultFU,Accu,NewResult),all_false_under(State,RestIntersection,Goal,NewResult,Result).
 check_empty(State,RestIntersection,Goal,Accu,Result, ResultFU).

check_empty(State,RestIntersection,Goal,Accu,Result, []) :- all_false_under(State,RestIntersection,Goal,Accu,Result), !.
check_empty(State,RestIntersection,Goal,Accu,Result, ResultFU) :-
	%writeln("resultfu"), writeln(ResultFU),
	append(Accu, ResultFU, Combined), append(Combined, [plusone], NewAccu),
	%writeln("newaccu"), writeln(NewAccu),
	all_false_under(State,RestIntersection,Goal,NewAccu,Result).

start_node((start,_,_)).

goal_node((_,State,_)):- goal_description(Goal), subtract(State, Goal, []).
 % "Zielbedingungen einlesen"
 % "Zustand gegen Zielbedingungen testen".



% Aufgrund der Komplexit�t der Zustandsbeschreibungen kann state_member nicht auf
% das Standardpr�dikat member zur�ckgef�hrt werden.
%
state_member(_,[]):- !, fail.

state_member(State,[FirstState|_]):- subtract(State, FirstState, []), !.
%  "Test, ob State bereits durch FirstState beschrieben war. Tipp: Eine
%  L�sungsm�glichkeit besteht in der Verwendung einer Mengenoperation,
%  z.B. subtract" ,!.

%Es ist sichergestellt, dass die beiden ersten Klauseln nicht zutreffen.
state_member(State,[_|RestStates]):- state_member(State, RestStates).
  %"rekursiver Aufruf".


eval_path([(_,State,Value3)|RestPath]) :-
	eval_path1([(_,State,Value3)|RestPath]).
	%write("Heuristic: "), writeln(Value3).
	%eval_path2([(_,State,Value2)|RestPath]),
	%writeln("~~~~~~~~~~~~~~~~~~~~~~~~~"),
	%write("Value2: "), writeln(Value2),
	%eval_path1([(_,State,Value1)|RestPath]).
	%write("Value1: "), writeln(Value1).

greedy_eval_path3([(_,State,Value)|_RestPath]) :-
	include(on_table, State, StateOnTable),
	goal_description(Goal),
	eval_path3_(StateOnTable, Goal, State, Value).


eval_path3([(_,State,Value)|RestPath]) :-
	include(on_table, State, StateOnTable),
	goal_description(Goal),
	eval_path3_(StateOnTable, Goal, State, Heuristic),
	length(RestPath, LengthRestPath),
	Value is Heuristic + LengthRestPath.

eval_path3_([], _Goal, _State, 0).
eval_path3_([Current | RestState], Goal, State, HeuristicR) :-
	count_amount_over_lowest_false(Current, Goal, State, Amount),
	eval_path3_(RestState, Goal, State, Heuristic),
	HeuristicR is Amount + Heuristic.

% is in right spot and has blocks above
count_amount_over_lowest_false(on(X, Y), Goal, State, Amount) :-
	member(on(X, Y), Goal),
	member(on(Y, Z), State), !,
	count_amount_over_lowest_false(on(Y, Z), Goal, State, Amount).
% is in right spot but has no block above
count_amount_over_lowest_false(on(X, Y), Goal, _State, 0) :-
	member(on(X, Y), Goal), !.
% is on wrong spot -> start counting
count_amount_over_lowest_false(on(X, Y), _Goal, State, AmountR) :-
	count_above(on(X, Y), State, Amount),
	AmountR is Amount + 1.

count_above(on(_X, Y), State, AmountR) :-
	member(on(Y, Z), State), !,
	count_above(on(Y, Z), State, Amount),
	AmountR is Amount + 1.
count_above(_OnStatement, _State, 0).




%%Kompliziertere Heuristik(funktioniert schlechter): Schaue dir an wie viele Bloecke unter "richtigen" Bloecken liegen und generiere daraus einen Wert
eval_path2([(_,State,Value)|RestPath]):-
        goal_description(Goal),
        intersection(Goal, State, Intersection),
        include(is_on, Intersection, Is_Ons_Intersection),
	all_false_under(State,Is_Ons_Intersection,Goal,[],Result),
	%write("All false under: "), writeln(Result),
        length(Result,LengthResult),
        length(RestPath, LengthRestPath),
	include(clear, Intersection, ClearIntersection),
	include(clear, Goal, ClearGoal),
	length(ClearIntersection, ClearIntersectionLength),
	length(ClearGoal, ClearGoalLength),
        Value is LengthResult*2+LengthRestPath + ClearGoalLength - ClearIntersectionLength.

%%Anzahl von noch falschen Zuständen
%
eval_path1_([(_,State,Value)|RestPath]):-
        goal_description(Goal),
        intersection(Goal, State, Intersection),
        length(Goal, LengthGoal),
        length(Intersection, LengthIntersection),
        length(RestPath, LengthRestPath),
        Value is LengthGoal - LengthIntersection + LengthRestPath.

greedy_eval_path1([(_,State,Value)|_RestPath]):-
        goal_description(Goal),
        include(is_on, Goal, Is_Ons_Goal),
        intersection(Goal, State, Intersection),
        include(is_on, Intersection, Is_Ons_Intersection),
        length(Is_Ons_Goal, LengthGoal),
        length(Is_Ons_Intersection, LengthIntersection),
        Value is LengthGoal - LengthIntersection.

eval_path1([(_,State,Value)|RestPath]):-
        goal_description(Goal),
        include(is_on, Goal, Is_Ons_Goal),
        intersection(Goal, State, Intersection),
        include(is_on, Intersection, Is_Ons_Intersection),
        length(Is_Ons_Goal, LengthGoal),
        length(Is_Ons_Intersection, LengthIntersection),
        length(RestPath, LengthRestPath),
        Value is LengthGoal - LengthIntersection + LengthRestPath.
 % eval_state(State,"Rest des Literals bzw. der Klausel"
 % "Value berechnen".



action(pick_up(X),                           % action name
       [handempty, clear(X), on(table,X)],   % Conditions / C
       [handempty, clear(X), on(table,X)],   % DeleteList / D
       [holding(X)]).                        % AddList / A

action(pick_up(X),
       [handempty, clear(X), on(Y,X), block(Y)],
       [handempty, clear(X), on(Y,X)],
       [holding(X), clear(Y)]).

action(put_on_table(X),
       [holding(X)],
       [holding(X)],
       [handempty, clear(X), on(table,X)]).

action(put_on(Y,X),
       [holding(X), clear(Y)],
       [holding(X), clear(Y)],
       [handempty, clear(X), on(Y,X)]).


% Hilfskonstrukt, weil das PROLOG "subset" nicht die Unifikation von Listenelementen
% durchf�hrt, wenn Variablen enthalten sind. "member" unifiziert hingegen.
%
mysubset([],_).
mysubset([H|T],List):-
  member(H,List),
  mysubset(T,List).


expand_help(State,Name,NewState):-
  %writeln("Start expand help"),
  action(Name, C, D, A),        % "Action suchen"
  %write(" Name: "), writeln(Name),
  %write(" C: "), writeln(C), write(" D "), writeln(D),
  mysubset(C, State),                                  % "Conditions testen"
  %writeln(State),
  %write(" Name: "), writeln(Name),
  %write("C nach mysubset: "), writeln(C),
  %write("D nach subset: "), writeln(D),
  %write("A nach subset: "), writeln(A),
  subtract(State, D, ResultFromDelete),                % "Del-List umsetzen"
  %write(" Result from delete: "), writeln(ResultFromDelete),
  append(ResultFromDelete, A, NewState).
  %write("new state: "), writeln(NewState).       % "Add-List umsetzen".
  %list_to_set(ResultFromAppend, NewState).   % we don't need this, yeah?
expand((_,State,_),Result):-
  findall((Name,NewState,_),expand_help(State,Name,NewState),Result).


























