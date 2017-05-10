<form action="feedback"+url+"" method="post" name="Email Response" target="_blank">&nbsp;
<input name="userId" type="hidden" value=<%=(String)request.getParameter("userId")%> />
<input name="courseId" type="hidden" value=<%=(String)request.getParameter("courseId")%> />
<div class="form-group">Rate the Course:
<div>
<label><input checked="checked" id="radios-0" name="score" type="radio" value="1" /> 1 </label> 
<label> <input id="radios-1" name="score" type="radio" value="2" /> 2 </label> 
<label> <input id="radios-2" name="score" type="radio" value="3" /> 3 </label> 
<label > <input id="radios-3" name="score" type="radio" value="4" /> 4</label>
<input id="radios-4" name="score" type="radio" value="5" /> 5
</div>
<div class="col-md-4">Comments:</div>
<div class="col-md-4"><textarea cols="100" name="feedback" rows="3"></textarea></div>
<div class="col-md-4"><input name="Submit" type="submit" value="Submit" /></div>
</div>
</form>