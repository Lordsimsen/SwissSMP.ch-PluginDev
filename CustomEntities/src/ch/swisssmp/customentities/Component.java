package ch.swisssmp.customentities;

public abstract class Component {
	protected abstract void Awake();
	protected abstract void Start();
	protected abstract void Update();
	protected abstract void OnDestroy();
}
