export interface PrintElement {
	id: number;
}


export class TextElement implements PrintElement {
	readonly id: number;
	readonly text: string;

	constructor(id: number, text: string) {
		this.id = id;
		this.text = text;
	}
}

export class QrCodeElement implements PrintElement {
	readonly id: number;
	readonly data: string;

	constructor(id: number, data: string) {
		this.id = id;
		this.data = data;
	}
}